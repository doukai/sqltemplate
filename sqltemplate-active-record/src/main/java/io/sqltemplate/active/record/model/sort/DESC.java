package io.sqltemplate.active.record.model.sort;

import com.google.common.base.CaseFormat;

public class DESC implements Sort {

    private final String columnName;

    public DESC(String columnName) {
        this.columnName = columnName;
    }

    public static DESC DESC(String columnName) {
        return new DESC(columnName);
    }

    @Override
    public String toString() {
        return "t." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " DESC";
    }
}
