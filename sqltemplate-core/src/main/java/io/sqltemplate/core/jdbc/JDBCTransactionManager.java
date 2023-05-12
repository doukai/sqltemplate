package io.sqltemplate.core.jdbc;

import io.sqltemplate.spi.transaction.JDBCConnectionCounter;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JDBCTransactionManager {

    private static final ThreadLocal<List<JDBCConnectionCounter>> connectionCounterThreadLocal = new ThreadLocal<>();

    private final static JDBCConnectionProvider connectionProvider = JDBCConnectionProvider.provider();

    public static List<JDBCConnectionCounter> getConnectionCounterList() {
        if (connectionCounterThreadLocal.get() == null) {
            connectionCounterThreadLocal.set(new ArrayList<>() {{
                add(new JDBCConnectionCounter(Objects.requireNonNull(connectionProvider).createConnection()));
            }});
        }
        return connectionCounterThreadLocal.get();
    }

    public static Connection getConnection() {
        List<JDBCConnectionCounter> connectionCounterList = getConnectionCounterList();
        return connectionCounterList.get(connectionCounterList.size() - 1).getConnection();
    }

    public static void begin(Transactional.TxType txType) throws SQLException, TransactionRequiredException, InvalidTransactionException, NotSupportedException {
        List<JDBCConnectionCounter> connectionCounterList = getConnectionCounterList();
        JDBCConnectionCounter connectionCounter = connectionCounterList.get(connectionCounterList.size() - 1);
        Connection newConnection;
        JDBCConnectionCounter newConnectionCounter;

        switch (txType) {
            case REQUIRED:
                if (connectionCounter.getCounter().getAndIncrement() == 0) {
                    connectionCounter.getConnection().setAutoCommit(false);
                }
                return;
            case REQUIRES_NEW:
                newConnection = Objects.requireNonNull(connectionProvider).createConnection();
                newConnectionCounter = new JDBCConnectionCounter(newConnection);
                connectionCounterList.add(newConnectionCounter);
                newConnectionCounter.getCounter().incrementAndGet();
                connectionCounter.getConnection().setAutoCommit(false);
                return;
            case MANDATORY:
                if (connectionCounter.getCounter().getAndIncrement() == 0) {
                    throw new TransactionRequiredException();
                }
                return;
            case SUPPORTS:
                return;
            case NOT_SUPPORTED:
                newConnection = Objects.requireNonNull(connectionProvider).createConnection();
                newConnectionCounter = new JDBCConnectionCounter(newConnection);
                connectionCounterList.add(newConnectionCounter);
                return;
            case NEVER:
                if (connectionCounter.getCounter().getAndIncrement() == 0) {
                    return;
                } else {
                    throw new InvalidTransactionException();
                }
            default:
                throw new NotSupportedException();
        }
    }

    public static void commit() throws SQLException {
        List<JDBCConnectionCounter> connectionCounterList = getConnectionCounterList();
        JDBCConnectionCounter connectionCounter = connectionCounterList.get(connectionCounterList.size() - 1);
        if (connectionCounter.getConnection().getAutoCommit()) {
            connectionCounterList.remove(connectionCounter);
            connectionCounter.getConnection().close();
        } else if (connectionCounter.getCounter().getAndDecrement() == 1) {
            connectionCounterList.remove(connectionCounter);
            connectionCounter.getConnection().commit();
            connectionCounter.getConnection().close();
        } else {
            connectionCounter.getCounter().decrementAndGet();
        }
    }

    public static void rollback(Throwable throwable, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        List<JDBCConnectionCounter> connectionCounterList = getConnectionCounterList();
        JDBCConnectionCounter connectionCounter = connectionCounterList.get(connectionCounterList.size() - 1);
        try {
            if (rollbackOn != null && rollbackOn.length > 0) {
                if (Arrays.stream(rollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {

                    connectionCounter.getConnection().rollback();
                } else {
                    connectionCounter.getConnection().commit();
                }
                connectionCounter.getConnection().close();
            } else if (dontRollbackOn != null && dontRollbackOn.length > 0) {
                if (Arrays.stream(dontRollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                    connectionCounter.getConnection().commit();
                } else {
                    connectionCounter.getConnection().rollback();
                }
                connectionCounter.getConnection().close();
            }
            connectionCounter.getConnection().rollback();
            connectionCounter.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
