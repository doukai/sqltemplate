package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class EQ implements Conditional {

    private final String columnName;
    private final Expression expression;

    public EQ(String columnName, Expression expression) {
        this.columnName = columnName;
        this.expression = expression;
    }

    public static EQ EQ(String columnName, Object expression) {
        return new EQ(columnName, Expression.of(expression));
    }

    @Override
    public String toString() {
        return columnName + " = " + expression;
    }
}
