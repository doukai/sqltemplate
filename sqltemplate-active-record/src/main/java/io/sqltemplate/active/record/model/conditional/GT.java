package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class GT extends Compare {

    public GT(String tableAlias, String columnName, Object expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String getSign() {
        return ">";
    }

    public static GT gt(String tableAlias, String columnName, Object expression) {
        return new GT(tableAlias, columnName, Expression.of(expression));
    }

    public static GT gt(String columnName, Object expression) {
        return new GT(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
