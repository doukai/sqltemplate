package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Value;

public class GTE implements Conditional {

    private final String columnName;
    private final Value value;

    public GTE(String columnName, Value value) {
        this.columnName = columnName;
        this.value = value;
    }

    public static GTE GTE(String columnName, Object value) {
        return new GTE(columnName, Value.of(value));
    }

    @Override
    public String toString() {
        return columnName + " >= " + value;
    }
}
