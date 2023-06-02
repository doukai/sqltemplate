package io.sqltemplate.active.record.model.conditional;

import java.util.List;

public abstract class Compare implements Conditional {

    private final String tableAlias;
    private final String columnName;
    private final Object expression;
    private final List<Object> expressions;

    public Compare(String tableAlias, String columnName, List<Object> expressions) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expression = null;
        this.expressions = expressions;
    }

    public Compare(String tableAlias, String columnName, Object expression) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expression = expression;
        this.expressions = null;
    }

    public Compare(String tableAlias, String columnName) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expression = null;
        this.expressions = null;
    }

    protected abstract String getSign();

    public String getTableAlias() {
        return tableAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getExpression() {
        return expression;
    }

    public List<Object> getExpressions() {
        return expressions;
    }

    @Override
    public boolean isCompare() {
        return true;
    }
}
