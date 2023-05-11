package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Value;

import java.util.Collection;
import java.util.stream.Collectors;

public class IN implements Conditional {

    private final String columnName;
    private final Collection<Value> values;

    public IN(String columnName, Collection<Value> values) {
        this.columnName = columnName;
        this.values = values;
    }

    public static IN IN(String columnName, Collection<Object> values) {
        return new IN(columnName, values.stream().map(Value::of).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return columnName + " IN (" + values.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
