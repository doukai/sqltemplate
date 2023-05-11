package io.sqltemplate.active.record.model.expression;

public class NumberValue implements Expression {

    private final Number value;

    public NumberValue(Number value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
