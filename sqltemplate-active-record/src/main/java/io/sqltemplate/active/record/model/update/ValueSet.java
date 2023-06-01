package io.sqltemplate.active.record.model.update;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class ValueSet {
    private final String tableAlias;
    private final String columnName;
    private final Parameter parameter;

    public ValueSet(String tableAlias, String columnName, Parameter parameter) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.parameter = parameter;
    }

    public static ValueSet set(String tableAlias, String columnName, Object value) {
        return new ValueSet(tableAlias, columnName, new Parameter(value));
    }

    public static ValueSet set(String columnName, Object value) {
        return new ValueSet(DEFAULT_ALIAS, columnName, new Parameter(value));
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public Parameter getParameter() {
        return parameter;
    }
}
