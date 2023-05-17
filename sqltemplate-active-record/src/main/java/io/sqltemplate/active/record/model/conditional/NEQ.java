package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class NEQ extends Compare {

    public NEQ(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "<>";
    }

    public static NEQ NEQ(String tableAlias, String columnName, Object expression) {
        return new NEQ(tableAlias, columnName, Expression.of(expression));
    }
}
