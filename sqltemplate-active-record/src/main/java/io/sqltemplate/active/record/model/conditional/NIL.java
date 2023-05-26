package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.expression.NullValue;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NIL extends Compare {

    public NIL(String tableAlias, String columnName) {
        super(tableAlias, columnName, new NullValue());
    }

    @Override
    protected String sign() {
        return "IS";
    }

    public static NIL nil(String tableAlias, String columnName) {
        return new NIL(tableAlias, columnName);
    }

    public static NIL nil(String columnName) {
        return new NIL(DEFAULT_ALIAS, columnName);
    }
}
