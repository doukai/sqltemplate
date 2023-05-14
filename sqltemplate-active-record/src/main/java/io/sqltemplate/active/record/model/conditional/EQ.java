package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class EQ extends Compare {

    public EQ(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return "=";
    }

    public static EQ EQ(String columnName, Object expression) {
        return new EQ(columnName, Expression.of(expression));
    }
}
