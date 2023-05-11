package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Value;

public class EQ implements Conditional {

    private final String columnName;
    private final Value value;

    public EQ(String columnName, Value value) {
        this.columnName = columnName;
        this.value = value;
    }

    public static EQ EQ(String columnName, Object value) {
        return new EQ(columnName, Value.of(value));
    }

    @Override
    public String toString() {
        return columnName + " = " + value;
    }
}
