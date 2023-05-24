package io.sqltemplate.spi.transaction;

import io.sqltemplate.spi.connection.JDBCConnectionProvider;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

public class JDBCTransactionManager {

    private static final ThreadLocal<List<JDBCTransactionConnection>> transactionConnectionListThreadLocal = new ThreadLocal<>();

    private final static JDBCConnectionProvider connectionProvider = JDBCConnectionProvider.provider();

    public static List<JDBCTransactionConnection> getTransactionConnectionList() {
        if (transactionConnectionListThreadLocal.get() == null) {
            transactionConnectionListThreadLocal.set(new ArrayList<JDBCTransactionConnection>());
        }
        return transactionConnectionListThreadLocal.get();
    }

    public static JDBCTransactionConnection getTransactionConnection() {
        List<JDBCTransactionConnection> transactionConnectionList = getTransactionConnectionList();
        return transactionConnectionList.get(transactionConnectionList.size() - 1);
    }

    public static Connection getConnection() {
        return getTransactionConnection().getConnection();
    }

    public static String begin() throws SQLException, TransactionRequiredException, InvalidTransactionException, NotSupportedException {
        return begin(REQUIRED);
    }

    public static String begin(Transactional.TxType txType) throws SQLException, TransactionRequiredException, InvalidTransactionException, NotSupportedException {
        String id = UUID.randomUUID().toString();
        List<JDBCTransactionConnection> transactionConnectionList = getTransactionConnectionList();
        Connection connection;
        switch (txType) {
            case REQUIRED:
                if (transactionConnectionList.size() == 0) {
                    connection = connectionProvider.createConnection();
                    connection.setAutoCommit(false);
                    transactionConnectionList.add(new JDBCTransactionConnection(id, connection));
                }
                return id;
            case REQUIRES_NEW:
                connection = connectionProvider.createConnection();
                connection.setAutoCommit(false);
                transactionConnectionList.add(new JDBCTransactionConnection(id, connection));
                return id;
            case MANDATORY:
                if (transactionConnectionList.size() == 0) {
                    throw new TransactionRequiredException();
                }
                return id;
            case SUPPORTS:
                if (transactionConnectionList.size() == 0) {
                    connection = connectionProvider.createConnection();
                    transactionConnectionList.add(new JDBCTransactionConnection(id, connection));
                }
                return id;
            case NOT_SUPPORTED:
                connection = connectionProvider.createConnection();
                transactionConnectionList.add(new JDBCTransactionConnection(id, connection));
                return id;
            case NEVER:
                if (transactionConnectionList.size() == 0) {
                    connection = connectionProvider.createConnection();
                    transactionConnectionList.add(new JDBCTransactionConnection(id, connection));
                    return id;
                } else {
                    throw new InvalidTransactionException();
                }
            default:
                throw new NotSupportedException();
        }
    }

    public static void commit(String id) throws SQLException {
        List<JDBCTransactionConnection> transactionConnectionList = getTransactionConnectionList();
        JDBCTransactionConnection transactionConnection = getTransactionConnection();
        if (transactionConnection.getConnection().getAutoCommit()) {
            transactionConnection.getConnection().close();
            transactionConnectionList.remove(transactionConnection);
        } else {
            if (transactionConnection.getId().equals(id)) {
                transactionConnection.getConnection().commit();
                transactionConnection.getConnection().close();
                transactionConnectionList.remove(transactionConnection);
            }
        }
    }

    public static void rollback(String id, Throwable throwable, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        List<JDBCTransactionConnection> transactionConnectionList = getTransactionConnectionList();
        if (transactionConnectionList.size() == 0) {
            throw new RuntimeException(throwable);
        }
        JDBCTransactionConnection transactionConnection = transactionConnectionList.get(transactionConnectionList.size() - 1);
        try {
            if (rollbackOn != null && rollbackOn.length > 0) {
                if (Arrays.stream(rollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                    transactionConnection.getConnection().rollback();
                    transactionConnection.getConnection().close();
                    transactionConnectionList.remove(transactionConnection);
                    throw new RuntimeException(throwable);
                } else {
                    commit(id);
                }
            } else if (dontRollbackOn != null && dontRollbackOn.length > 0) {
                if (Arrays.stream(dontRollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                    commit(id);
                } else {
                    transactionConnection.getConnection().rollback();
                    transactionConnection.getConnection().close();
                    transactionConnectionList.remove(transactionConnection);
                    throw new RuntimeException(throwable);
                }
            } else {
                transactionConnection.getConnection().rollback();
                transactionConnection.getConnection().close();
                transactionConnectionList.remove(transactionConnection);
                throw new RuntimeException(throwable);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
