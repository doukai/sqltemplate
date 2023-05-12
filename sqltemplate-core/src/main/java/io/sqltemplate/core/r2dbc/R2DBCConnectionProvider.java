package io.sqltemplate.core.r2dbc;

import io.r2dbc.spi.Connection;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.ServiceLoader;

public interface R2DBCConnectionProvider {

    Mono<Connection> createConnection();

    static R2DBCConnectionProvider provider() {
        ServiceLoader<R2DBCConnectionProvider> loader = ServiceLoader.load(R2DBCConnectionProvider.class);
        Iterator<R2DBCConnectionProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}
