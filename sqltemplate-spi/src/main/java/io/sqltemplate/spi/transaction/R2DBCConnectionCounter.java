package io.sqltemplate.spi.transaction;

import io.r2dbc.spi.Connection;

import java.util.concurrent.atomic.AtomicInteger;

public class R2DBCConnectionCounter {

    private Connection connection;

    private AtomicInteger counter;

    public R2DBCConnectionCounter(Connection connection) {
        this.connection = connection;
        this.counter = new AtomicInteger(0);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }
}
