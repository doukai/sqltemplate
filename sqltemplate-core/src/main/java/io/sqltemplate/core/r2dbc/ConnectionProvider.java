package io.sqltemplate.core.r2dbc;

import io.r2dbc.spi.Connection;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.ServiceLoader;

public interface ConnectionProvider {

    Mono<Connection> createConnection();

    static ConnectionProvider provider() {
        ServiceLoader<ConnectionProvider> loader = ServiceLoader.load(ConnectionProvider.class);
        Iterator<ConnectionProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}
