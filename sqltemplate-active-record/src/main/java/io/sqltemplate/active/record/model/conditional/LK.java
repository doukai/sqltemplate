package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class LK extends Compare {

    public LK(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return "LIKE";
    }

    public static LK LK(String columnName, Object expression) {
        return new LK(columnName, Expression.of(expression));
    }
}
