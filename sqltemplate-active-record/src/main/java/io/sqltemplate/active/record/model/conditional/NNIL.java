package io.sqltemplate.active.record.model.conditional;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NNIL extends Compare {

    public NNIL(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    @Override
    protected String getSign() {
        return "IS NOT";
    }

    public static NNIL nnil(String tableAlias, String columnName) {
        return new NNIL(tableAlias, columnName);
    }

    public static NNIL nnil(String columnName) {
        return new NNIL(DEFAULT_ALIAS, columnName);
    }
}
