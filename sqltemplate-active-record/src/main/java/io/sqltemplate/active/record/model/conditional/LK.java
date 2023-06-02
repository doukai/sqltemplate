package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class LK extends Compare {

    public LK(String tableAlias, String columnName, Object expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String getSign() {
        return "LIKE";
    }

    public static LK lk(String tableAlias, String columnName, Object expression) {
        return new LK(tableAlias, columnName, Expression.of(expression));
    }

    public static LK lk(String columnName, Object expression) {
        return new LK(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
