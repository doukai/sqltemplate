package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import java.util.Collection;
import java.util.stream.Collectors;

public class NIN implements Conditional {

    private final String columnName;
    private final Collection<Expression> expressions;

    public NIN(String columnName, Collection<Expression> expressions) {
        this.columnName = columnName;
        this.expressions = expressions;
    }

    public static NIN NIN(String columnName, Collection<Object> expressions) {
        return new NIN(columnName, expressions.stream().map(Expression::of).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return columnName + " NOT IN (" + expressions.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
