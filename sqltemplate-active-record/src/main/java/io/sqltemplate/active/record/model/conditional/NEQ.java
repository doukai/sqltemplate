package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class NEQ extends Compare {

    public NEQ(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return "<>";
    }

    public static NEQ NEQ(String columnName, Object expression) {
        return new NEQ(columnName, Expression.of(expression));
    }
}
