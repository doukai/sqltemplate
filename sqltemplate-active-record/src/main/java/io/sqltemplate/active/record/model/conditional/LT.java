package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class LT extends Compare {

    public LT(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return "<";
    }

    public static LT LT(String columnName, Object expression) {
        return new LT(columnName, Expression.of(expression));
    }
}
