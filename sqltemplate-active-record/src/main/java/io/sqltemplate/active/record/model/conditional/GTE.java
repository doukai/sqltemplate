package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class GTE extends Compare {

    public GTE(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return ">=";
    }

    public static GTE GTE(String columnName, Object expression) {
        return new GTE(columnName, Expression.of(expression));
    }
}
