package io.sqltemplate.core.r2dbc;

import jakarta.transaction.Transactional;

import java.util.Map;

public class DefaultR2DBCAdapter<T> extends R2DBCAdapter<T> {

    public DefaultR2DBCAdapter() {
    }

    public DefaultR2DBCAdapter(String templateName, String instanceName, Map<String, Object> params) {
        super(templateName, instanceName, params);
    }

    public DefaultR2DBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        super(templateName, instanceName, params, txType, rollbackOn, dontRollbackOn);
    }

    public DefaultR2DBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional transactional) {
        super(templateName, instanceName, params, transactional);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T map(Map<String, Object> result) {
        return (T) result;
    }
}
