package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NLK extends Compare {

    public NLK(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "NOT LIKE";
    }

    public static NLK nlk(String tableAlias, String columnName, Object expression) {
        return new NLK(tableAlias, columnName, Expression.of(expression));
    }

    public static NLK nlk(String columnName, Object expression) {
        return new NLK(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
