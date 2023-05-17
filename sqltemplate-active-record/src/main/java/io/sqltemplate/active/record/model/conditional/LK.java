package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class LK extends Compare {

    public LK(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "LIKE";
    }

    public static LK LK(String tableAlias, String columnName, Object expression) {
        return new LK(tableAlias, columnName, Expression.of(expression));
    }
}
