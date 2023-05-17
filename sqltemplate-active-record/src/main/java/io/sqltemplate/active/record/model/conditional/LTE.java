package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class LTE extends Compare {

    public LTE(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "<=";
    }

    public static LTE LTE(String tableAlias, String columnName, Object expression) {
        return new LTE(tableAlias, columnName, Expression.of(expression));
    }
}
