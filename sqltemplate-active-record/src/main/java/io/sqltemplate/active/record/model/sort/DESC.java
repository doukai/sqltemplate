package io.sqltemplate.active.record.model.sort;

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
        return columnName + " DESC";
    }
}
