package io.sqltemplate.active.record.model.sort;

import com.google.common.base.CaseFormat;

public class DESC implements Sort {

    private final String tableAlias;
    private final String columnName;

    public DESC(String tableAlias, String columnName) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    public static DESC DESC(String tableAlias, String columnName) {
        return new DESC(tableAlias, columnName);
    }

    @Override
    public String toString() {
        return tableAlias + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " DESC";
    }
}
