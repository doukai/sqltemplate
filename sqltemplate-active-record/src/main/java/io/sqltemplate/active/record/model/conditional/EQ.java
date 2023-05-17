package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class EQ extends Compare {

    public EQ(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "=";
    }

    public static EQ EQ(String tableAlias, String columnName, Object expression) {
        return new EQ(tableAlias, columnName, Expression.of(expression));
    }
}
