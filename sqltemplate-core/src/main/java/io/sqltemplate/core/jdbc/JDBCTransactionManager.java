package io.sqltemplate.core.jdbc;

import io.sqltemplate.spi.transaction.JDBCConnectionCounter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class JDBCTransactionManager {

    private static final ThreadLocal<JDBCConnectionCounter> connectionCounterThreadLocal = new ThreadLocal<>();

    private final static JDBCConnectionProvider jdbcConnectionProvider = JDBCConnectionProvider.provider();

    public static JDBCConnectionCounter getConnectionCounter() {
        if (connectionCounterThreadLocal.get() == null) {
            connectionCounterThreadLocal.set(new JDBCConnectionCounter(Objects.requireNonNull(jdbcConnectionProvider).createConnection()));
        }
        return connectionCounterThreadLocal.get();
    }

    public static Connection getConnection() {
        return getConnectionCounter().getConnection();
    }

    public static void begin() {
        try {
            JDBCConnectionCounter connectionCounter = getConnectionCounter();
            if (connectionCounter.getCounter().getAndIncrement() == 0) {
                connectionCounter.getConnection().setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void commit() {
        try {
            JDBCConnectionCounter connectionCounter = getConnectionCounter();
            if (connectionCounter.getCounter().getAndDecrement() == 1) {
                Connection connection = connectionCounter.getConnection();
                connection.commit();
                connection.close();
                connectionCounterThreadLocal.remove();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rollback(Throwable throwable) {
        try {
            JDBCConnectionCounter connectionCounter = getConnectionCounter();
            connectionCounter.getCounter().set(0);
            Connection connection = connectionCounter.getConnection();
            connection.rollback();
            connection.close();
            connectionCounterThreadLocal.remove();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
