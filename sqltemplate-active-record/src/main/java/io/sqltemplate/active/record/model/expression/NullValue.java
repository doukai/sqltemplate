package io.sqltemplate.active.record.model.expression;

public class NullValue implements Expression {
    @Override
    public String toString() {
        return "NULL";
    }
}
