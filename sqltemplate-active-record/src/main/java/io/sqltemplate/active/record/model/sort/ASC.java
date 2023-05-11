package io.sqltemplate.active.record.model.sort;

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
        return columnName + " ASC";
    }
}
