package io.sqltemplate.runtime.handler.interceptor;

import io.sqltemplate.core.adapter.Adapter;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class QueryInterceptor extends BaseInterceptor {

    public QueryInterceptor(Adapter<?> adapter, String templateName, String instanceName, String[] argumentNames) {
        super(adapter, templateName, instanceName, argumentNames);
    }

    public QueryInterceptor(Adapter<?> adapter, String templateName, String instanceName, String[] argumentNames, Transactional transactional) {
        super(adapter, templateName, instanceName, argumentNames, transactional);
    }

    @RuntimeType
    public Object interceptor(@AllArguments Object[] args) {
        return getJDBCAdapter(args).query();
    }
}
