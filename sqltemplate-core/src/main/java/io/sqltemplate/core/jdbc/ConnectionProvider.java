package io.sqltemplate.core.jdbc;

import java.sql.Connection;
import java.util.Iterator;
import java.util.ServiceLoader;

public interface ConnectionProvider {

    Connection createConnection();

    static ConnectionProvider provider() {
        ServiceLoader<ConnectionProvider> loader = ServiceLoader.load(ConnectionProvider.class);
        Iterator<ConnectionProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}
