package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.NullValue;

public class NIL extends Compare {

    public NIL(String tableAlias, String columnName) {
        super(tableAlias, columnName, new NullValue());
    }

    @Override
    protected String sign() {
        return "IS";
    }

    public static NIL NIL(String tableAlias, String columnName) {
        return new NIL(tableAlias, columnName);
    }
}
