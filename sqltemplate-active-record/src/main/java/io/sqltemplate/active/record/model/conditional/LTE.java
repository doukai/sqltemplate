package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class LTE extends Compare {

    public LTE(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return "<=";
    }

    public static LTE lte(String tableAlias, String columnName, Object expression) {
        return new LTE(tableAlias, columnName, Expression.of(expression));
    }

    public static LTE lte(String columnName, Object expression) {
        return new LTE(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
