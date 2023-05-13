package io.sqltemplate.runtime.handler.interceptor;

import io.sqltemplate.core.adapter.Adapter;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class UpdateMonoInterceptor extends BaseInterceptor {

    public UpdateMonoInterceptor(Adapter<?> adapter, String templateName, String instanceName, String[] argumentNames) {
        super(adapter, templateName, instanceName, argumentNames);
    }

    public UpdateMonoInterceptor(Adapter<?> adapter, String templateName, String instanceName, String[] argumentNames, Transactional transactional) {
        super(adapter, templateName, instanceName, argumentNames, transactional);
    }

    @RuntimeType
    public Object interceptor(@AllArguments Object[] args) {
        return getR2DBCAdapter(args).update();
    }
}
