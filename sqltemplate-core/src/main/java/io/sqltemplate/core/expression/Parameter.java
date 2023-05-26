package io.sqltemplate.core.expression;

public class Parameter implements Expression {

    private final Object value;

    public Parameter(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
