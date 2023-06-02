package io.sqltemplate.active.record.model.update;

import io.sqltemplate.active.record.model.expression.Expression;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class ValueSet {
    private final String tableAlias;
    private final String columnName;
    private final Object expression;

    public ValueSet(String tableAlias, String columnName, Object expression) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expression = expression;
    }

    public static ValueSet set(String tableAlias, String columnName, Object expression) {
        return new ValueSet(tableAlias, columnName, Expression.of(expression));
    }

    public static ValueSet set(String columnName, Object expression) {
        return new ValueSet(DEFAULT_ALIAS, columnName, Expression.of(expression));
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getExpression() {
        return expression;
    }
}
