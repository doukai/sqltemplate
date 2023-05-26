package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class EQ extends Compare {

    public EQ(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "=";
    }

    public static EQ eq(String tableAlias, String columnName, Object expression) {
        return new EQ(tableAlias, columnName, Expression.of(expression));
    }

    public static EQ eq(String columnName, Object expression) {
        return new EQ(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
