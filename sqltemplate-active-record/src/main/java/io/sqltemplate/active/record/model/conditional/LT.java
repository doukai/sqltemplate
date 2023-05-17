package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class LT extends Compare {

    public LT(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "<";
    }

    public static LT LT(String tableAlias, String columnName, Object expression) {
        return new LT(tableAlias, columnName, Expression.of(expression));
    }
}
