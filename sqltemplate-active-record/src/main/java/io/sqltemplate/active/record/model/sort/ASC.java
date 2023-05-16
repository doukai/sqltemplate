package io.sqltemplate.active.record.model.sort;

import com.google.common.base.CaseFormat;

public class ASC implements Sort {

    private final String columnName;

    public ASC(String columnName) {
        this.columnName = columnName;
    }

    public static ASC ASC(String columnName) {
        return new ASC(columnName);
    }

    @Override
    public String toString() {
        return "t." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " ASC";
    }
}
