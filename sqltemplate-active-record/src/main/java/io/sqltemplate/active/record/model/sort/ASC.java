package io.sqltemplate.active.record.model.sort;

import com.google.common.base.CaseFormat;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class ASC implements Sort {

    private final String tableAlias;
    private final String columnName;

    public ASC(String tableAlias, String columnName) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    public static ASC asc(String tableAlias, String columnName) {
        return new ASC(tableAlias, columnName);
    }

    public static ASC asc(String columnName) {
        return new ASC(DEFAULT_ALIAS, columnName);
    }

    @Override
    public String toString() {
        return tableAlias + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " ASC";
    }
}
