package io.sqltemplate.core.r2dbc;

import java.util.Map;

public class DefaultR2DBCAdapter<T> extends R2DBCAdapter<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected T map(Map<String, Object> result) {
        return (T) result;
    }
}
