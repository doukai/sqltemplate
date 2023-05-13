package io.sqltemplate.runtime.handler.interceptor;

import io.sqltemplate.core.adapter.Adapter;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class QueryListInterceptor extends BaseInterceptor {

    public QueryListInterceptor(Adapter<?> adapter, String templateName, String instanceName, String[] argumentNames) {
        super(adapter, templateName, instanceName, argumentNames);
    }

    public QueryListInterceptor(Adapter<?> adapter, String templateName, String instanceName, String[] argumentNames, Transactional transactional) {
        super(adapter, templateName, instanceName, argumentNames, transactional);
    }

    @RuntimeType
    public Object interceptor(@AllArguments Object[] args) {
        return getJDBCAdapter(args).queryList();
    }
}
