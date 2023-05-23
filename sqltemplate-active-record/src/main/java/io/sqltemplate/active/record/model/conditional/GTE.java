package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class GTE extends Compare {

    public GTE(String tableAlias, String columnName, Expression expression) {
        super(tableAlias, columnName, expression);
    }

    @Override
    protected String sign() {
        return ">=";
    }

    public static GTE gte(String tableAlias, String columnName, Object expression) {
        return new GTE(tableAlias, columnName, Expression.of(expression));
    }

    public static GTE gte(String columnName, Object expression) {
        return new GTE(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }
}
