package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class GTE extends Compare {

    public GTE(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return ">=";
    }

    public static GTE GTE(String tableAlias, String columnName, Object expression) {
        return new GTE(tableAlias, columnName, Expression.of(expression));
    }
}
