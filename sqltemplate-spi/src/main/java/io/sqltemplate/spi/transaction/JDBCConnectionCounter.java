package io.sqltemplate.spi.transaction;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

public class JDBCConnectionCounter {

    private Connection connection;

    private AtomicInteger counter;

    public JDBCConnectionCounter(Connection connection) {
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
