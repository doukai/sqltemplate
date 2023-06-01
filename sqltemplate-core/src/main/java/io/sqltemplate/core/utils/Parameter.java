package io.sqltemplate.core.utils;

public class Parameter {

    private final Object value;

    public Parameter(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
