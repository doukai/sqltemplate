package io.sqltemplate.active.record.model.update;

import io.sqltemplate.active.record.model.expression.Value;

public class ValueSet {
    private final String columnName;
    private final Value value;

    public ValueSet(String columnName, Value value) {
        this.columnName = columnName;
        this.value = value;
    }

    public static ValueSet SET(String columnName, Object value) {
        return new ValueSet(columnName, Value.of(value));
    }

    @Override
    public String toString() {
        return columnName + " = " + value;
    }
}
