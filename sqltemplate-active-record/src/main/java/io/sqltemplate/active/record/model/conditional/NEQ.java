package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NEQ extends Compare {

    public NEQ(String tableAlias, String columnName, Object expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String getSign() {
        return "<>";
    }

    public static NEQ neq(String tableAlias, String columnName, Object expression) {
        return new NEQ(tableAlias, columnName, Expression.of(expression));
    }

    public static NEQ neq(String columnName, Object expression) {
        return new NEQ(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
