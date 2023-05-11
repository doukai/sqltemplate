package io.sqltemplate.active.record.model.update;

import io.sqltemplate.active.record.model.expression.Value;

import java.util.List;
import java.util.stream.IntStream;

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

    public static ValueSet[] SETS(List<String> columnNames, List<Object> values) {
        return IntStream.range(0, columnNames.size())
                .mapToObj(index -> SET(columnNames.get(index), values.get(index)))
                .toArray(ValueSet[]::new);
    }

    @Override
    public String toString() {
        return columnName + " = " + value;
    }
}
