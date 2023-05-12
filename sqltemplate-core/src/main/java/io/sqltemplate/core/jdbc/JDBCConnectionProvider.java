package io.sqltemplate.core.jdbc;

import java.sql.Connection;
import java.util.Iterator;
import java.util.ServiceLoader;

public interface JDBCConnectionProvider {

    Connection createConnection();

    static JDBCConnectionProvider provider() {
        ServiceLoader<JDBCConnectionProvider> loader = ServiceLoader.load(JDBCConnectionProvider.class);
        Iterator<JDBCConnectionProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}
