package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class GT extends Compare {

    public GT(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return ">";
    }

    public static GT GT(String columnName, Object expression) {
        return new GT(columnName, Expression.of(expression));
    }
}
