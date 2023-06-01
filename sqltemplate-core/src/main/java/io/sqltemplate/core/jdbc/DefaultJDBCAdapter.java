package io.sqltemplate.core.jdbc;

import jakarta.transaction.Transactional;

import java.util.Map;

public class DefaultJDBCAdapter<T> extends JDBCAdapter<T> {

    public DefaultJDBCAdapter() {
    }

    public DefaultJDBCAdapter(String templateName, String instanceName, Map<String, Object> params) {
        super(templateName, instanceName, params);
    }

    public DefaultJDBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        super(templateName, instanceName, params, txType, rollbackOn, dontRollbackOn);
    }

    public DefaultJDBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional transactional) {
        super(templateName, instanceName, params, transactional);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T map(Map<String, Object> result) {
        return (T) result;
    }
}
