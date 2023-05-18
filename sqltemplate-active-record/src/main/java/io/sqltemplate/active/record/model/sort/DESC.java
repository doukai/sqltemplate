package io.sqltemplate.active.record.model.sort;

import com.google.common.base.CaseFormat;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class DESC implements Sort {

    private final String tableAlias;
    private final String columnName;

    public DESC(String tableAlias, String columnName) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    public static DESC desc(String tableAlias, String columnName) {
        return new DESC(tableAlias, columnName);
    }

    public static DESC desc(String columnName) {
        return new DESC(DEFAULT_ALIAS, columnName);
    }

    @Override
    public String toString() {
        return tableAlias + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " DESC";
    }
}
