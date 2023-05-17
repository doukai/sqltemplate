package io.sqltemplate.active.record.model.sort;

import com.google.common.base.CaseFormat;

public class ASC implements Sort {

    private final String tableAlias;
    private final String columnName;

    public ASC(String tableAlias, String columnName) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    public static ASC ASC(String tableAlias, String columnName) {
        return new ASC(tableAlias, columnName);
    }

    @Override
    public String toString() {
        return tableAlias + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " ASC";
    }
}
