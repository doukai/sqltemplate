package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class NLK extends Compare {

    public NLK(String columnName, Expression expression) {
        super(columnName, expression);
    }

    @Override
    protected String sign() {
        return "NOT LIKE";
    }

    public static NLK NLK(String columnName, Object expression) {
        return new NLK(columnName, Expression.of(expression));
    }
}
