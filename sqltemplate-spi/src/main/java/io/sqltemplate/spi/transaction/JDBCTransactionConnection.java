package io.sqltemplate.spi.transaction;

import java.sql.Connection;

public class JDBCTransactionConnection {

    private final String id;

    private Connection connection;

    public JDBCTransactionConnection(String id, Connection connection) {
        this.id = id;
        this.connection = connection;
    }

    public String getId() {
        return id;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
