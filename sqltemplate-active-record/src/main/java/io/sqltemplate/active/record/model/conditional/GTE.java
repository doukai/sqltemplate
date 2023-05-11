package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class GTE implements Conditional {

    private final String columnName;
    private final Expression expression;

    public GTE(String columnName, Expression expression) {
        this.columnName = columnName;
        this.expression = expression;
    }

    public static GTE GTE(String columnName, Object expression) {
        return new GTE(columnName, Expression.of(expression));
    }

    @Override
    public String toString() {
        return columnName + " >= " + expression;
    }
}
