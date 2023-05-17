package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

public class NLK extends Compare {

    public NLK(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "NOT LIKE";
    }

    public static NLK NLK(String tableAlias, String columnName, Object expression) {
        return new NLK(tableAlias, columnName, Expression.of(expression));
    }
}
