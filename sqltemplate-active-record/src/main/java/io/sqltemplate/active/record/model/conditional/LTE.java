package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class LTE extends Compare {

    public LTE(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return "<=";
    }

    public static LTE LTE(String columnName, Object expression) {
        return new LTE(columnName, Expression.of(expression));
    }
}
