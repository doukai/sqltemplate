package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class LT extends Compare {

    public LT(String tableAlias, String columnName, Object expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String getSign() {
        return "<";
    }

    public static LT lt(String tableAlias, String columnName, Object expression) {
        return new LT(tableAlias, columnName, Expression.of(expression));
    }

    public static LT lt(String columnName, Object expression) {
        return new LT(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
