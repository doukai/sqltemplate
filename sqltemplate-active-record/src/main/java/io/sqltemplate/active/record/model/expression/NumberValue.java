package io.sqltemplate.active.record.model.expression;

public class NumberValue implements Value {

    private final Number value;

    public NumberValue(Number value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
