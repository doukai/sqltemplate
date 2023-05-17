package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.NullValue;

public class NNIL extends Compare {

    public NNIL(String tableAlias, String columnName) {
        super(tableAlias, columnName, new NullValue());
    }

    @Override
    protected String sign() {
        return "IS NOT";
    }

    public static NNIL NNIL(String tableAlias, String columnName) {
        return new NNIL(tableAlias, columnName);
    }
}
