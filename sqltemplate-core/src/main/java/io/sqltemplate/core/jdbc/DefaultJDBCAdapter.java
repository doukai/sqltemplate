package io.sqltemplate.core.jdbc;

import io.sqltemplate.core.r2dbc.R2DBCAdapter;

import java.util.Map;

public class DefaultJDBCAdapter<T> extends R2DBCAdapter<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected T map(Map<String, Object> result) {
        return (T) result;
    }
}
