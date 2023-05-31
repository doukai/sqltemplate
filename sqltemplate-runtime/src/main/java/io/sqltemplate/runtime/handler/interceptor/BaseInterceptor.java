package io.sqltemplate.runtime.handler.interceptor;

import io.sqltemplate.core.adapter.Adapter;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import jakarta.transaction.Transactional;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseInterceptor {

    private final Supplier<Adapter<?>> adapterSupplier;

    private final String templateName;
    private final String instanceName;
    private final String[] argumentNames;

    private Transactional.TxType txType = Transactional.TxType.REQUIRED;
    private Class<?>[] rollbackOn = {};
    private Class<?>[] dontRollbackOn = {};

    public BaseInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames) {
        this.adapterSupplier = adapterSupplier;
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.argumentNames = argumentNames;
    }

    public BaseInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        this.adapterSupplier = adapterSupplier;
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.argumentNames = argumentNames;
        this.txType = txType;
        this.rollbackOn = rollbackOn;
        this.dontRollbackOn = dontRollbackOn;
    }

    public BaseInterceptor(Supplier<Adapter<?>> adapterSupplier, String templateName, String instanceName, String[] argumentNames, Transactional transactional) {
        this.adapterSupplier = adapterSupplier;
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.argumentNames = argumentNames;
        this.txType = transactional.value();
        this.rollbackOn = transactional.rollbackOn();
        this.dontRollbackOn = transactional.dontRollbackOn();
    }

    public Supplier<Adapter<?>> getAdapterSupplier() {
        return adapterSupplier;
    }

    public R2DBCAdapter<?> getR2DBCAdapter(Object[] args) {
        return (R2DBCAdapter<?>) getAdapterSupplier().get().setTemplateName(templateName)
                .setInstanceName(instanceName)
                .setParams(getParams(args))
                .setTxType(txType)
                .setRollbackOn(rollbackOn)
                .setDontRollbackOn(dontRollbackOn);
    }

    public JDBCAdapter<?> getJDBCAdapter(Object[] args) {
        return (JDBCAdapter<?>) getAdapterSupplier().get().setTemplateName(templateName)
                .setInstanceName(instanceName)
                .setParams(getParams(args))
                .setTxType(txType)
                .setRollbackOn(rollbackOn)
                .setDontRollbackOn(dontRollbackOn);
    }

    private Map<String, Object> getParams(Object[] args) {
        return IntStream.range(0, getArgumentNames().length)
                .mapToObj(index -> {
                            String argumentName = getArgumentNames()[index];
                            return new AbstractMap.SimpleEntry<>(argumentName, args[index]);
                        }
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String[] getArgumentNames() {
        return argumentNames;
    }

    public Transactional.TxType getTxType() {
        return txType;
    }

    public void setTxType(Transactional.TxType txType) {
        this.txType = txType;
    }

    public Class<?>[] getRollbackOn() {
        return rollbackOn;
    }

    public void setRollbackOn(Class<?>[] rollbackOn) {
        this.rollbackOn = rollbackOn;
    }

    public Class<?>[] getDontRollbackOn() {
        return dontRollbackOn;
    }

    public void setDontRollbackOn(Class<?>[] dontRollbackOn) {
        this.dontRollbackOn = dontRollbackOn;
    }
}
