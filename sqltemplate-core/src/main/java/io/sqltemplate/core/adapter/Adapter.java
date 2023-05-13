package io.sqltemplate.core.adapter;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Adapter<T> {

    private String templateName;
    private String instanceName;
    private Map<String, Object> params;

    private Transactional.TxType txType = Transactional.TxType.REQUIRED;
    private Class<?>[] rollbackOn = {};
    private Class<?>[] dontRollbackOn = {};

    public Adapter() {
    }

    public Adapter(String templateName, String instanceName, Map<String, Object> params) {
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.params = params;
    }

    public Adapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.params = params;
        this.txType = txType;
        this.rollbackOn = rollbackOn;
        this.dontRollbackOn = dontRollbackOn;
    }

    public Adapter(String templateName, String instanceName, Map<String, Object> params, Transactional transactional) {
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.params = params;
        this.txType = transactional.value();
        this.rollbackOn = transactional.rollbackOn();
        this.dontRollbackOn = transactional.dontRollbackOn();
    }

    public String getTemplateName() {
        return templateName;
    }

    public Adapter<T> setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public Adapter<T> setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Adapter<T> setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Transactional.TxType getTxType() {
        return txType;
    }

    public Adapter<T> setTxType(Transactional.TxType txType) {
        this.txType = txType;
        return this;
    }

    public Class<?>[] getRollbackOn() {
        return rollbackOn;
    }

    public Adapter<T> setRollbackOn(Class<?>[] rollbackOn) {
        this.rollbackOn = rollbackOn;
        return this;
    }

    public Class<?>[] getDontRollbackOn() {
        return dontRollbackOn;
    }

    public Adapter<T> setDontRollbackOn(Class<?>[] dontRollbackOn) {
        this.dontRollbackOn = dontRollbackOn;
        return this;
    }

    @SuppressWarnings("unchecked")
    protected T map(Map<String, Object> result) {
        return (T) result;
    }

    protected List<T> mapList(List<Map<String, Object>> list) {
        return list.stream().map(this::map).collect(Collectors.toList());
    }
}
