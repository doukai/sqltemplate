package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

public abstract class Compare implements Conditional {

    private final String tableAlias;
    private final String columnName;
    private final Parameter parameter;

    public Compare(String tableAlias, String columnName, Parameter parameter) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.parameter = parameter;
    }

    protected abstract String sign();

    public String getTableAlias() {
        return tableAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public boolean isCompare() {
        return true;
    }
}
