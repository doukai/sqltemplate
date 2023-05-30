package io.sqltemplate.core.jdbc;

import java.util.Map;

public class DefaultJDBCAdapter<T> extends JDBCAdapter<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected T map(Map<String, Object> result) {
        return (T) result;
    }
}
