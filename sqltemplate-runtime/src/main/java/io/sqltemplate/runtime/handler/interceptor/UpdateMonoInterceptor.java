package io.sqltemplate.runtime.handler.interceptor;

import io.sqltemplate.core.adapter.Adapter;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.util.function.Supplier;

public class UpdateMonoInterceptor extends BaseInterceptor {

    public UpdateMonoInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames) {
        super(adapterSupplier, templateName, instanceName, argumentNames);
    }

    public UpdateMonoInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        super(adapterSupplier, templateName, instanceName, argumentNames, txType, rollbackOn, dontRollbackOn);
    }

    public UpdateMonoInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames, Transactional transactional) {
        super(adapterSupplier, templateName, instanceName, argumentNames, transactional);
    }

    @RuntimeType
    public Object interceptor(@AllArguments Object[] args) {
        return getR2DBCAdapter(args).update();
    }
}
