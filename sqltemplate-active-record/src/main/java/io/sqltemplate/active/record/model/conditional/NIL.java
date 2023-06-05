package io.sqltemplate.active.record.model.conditional;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NIL extends Compare {

    public NIL(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    @Override
    public String getSign() {
        return "IS";
    }

    public static NIL nil(String tableAlias, String columnName) {
        return new NIL(tableAlias, columnName);
    }

    public static NIL nil(String columnName) {
        return new NIL(DEFAULT_ALIAS, columnName);
    }
}
