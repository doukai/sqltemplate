package io.sqltemplate.runtime.handler.interceptor;

import io.sqltemplate.core.adapter.Adapter;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.util.function.Supplier;

public class QueryMonoListInterceptor extends BaseInterceptor {

    public QueryMonoListInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames) {
        super(adapterSupplier, templateName, instanceName, argumentNames);
    }

    public QueryMonoListInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        super(adapterSupplier, templateName, instanceName, argumentNames, txType, rollbackOn, dontRollbackOn);
    }

    public QueryMonoListInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames, Transactional transactional) {
        super(adapterSupplier, templateName, instanceName, argumentNames, transactional);
    }

    @RuntimeType
    public Object interceptor(@AllArguments Object[] args) {
        return getR2DBCAdapter(args).queryList();
    }
}
