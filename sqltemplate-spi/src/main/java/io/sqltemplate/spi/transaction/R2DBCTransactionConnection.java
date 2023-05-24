package io.sqltemplate.spi.transaction;

import io.r2dbc.spi.Connection;

public class R2DBCTransactionConnection {

    private final String id;

    private Connection connection;

    public R2DBCTransactionConnection(String id, Connection connection) {
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
