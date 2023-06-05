package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class EQ extends Compare {

    public EQ(String tableAlias, String columnName, Object expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    public String getSign() {
        return "=";
    }

    public static EQ eq(String tableAlias, String columnName, Object expression) {
        return new EQ(tableAlias, columnName, Expression.of(expression));
    }

    public static EQ eq(String columnName, Object expression) {
        return new EQ(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
