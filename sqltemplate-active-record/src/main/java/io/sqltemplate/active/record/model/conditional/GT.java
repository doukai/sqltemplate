package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class GT extends Compare {

    public GT(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return ">";
    }

    public static GT GT(String tableAlias, String columnName, Object expression) {
        return new GT(tableAlias, columnName, Expression.of(expression));
    }
}
