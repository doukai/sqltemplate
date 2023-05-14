package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.NullValue;

public class NNIL extends Compare {

    public NNIL(String columnName) {
        super(columnName, new NullValue());
    }

    @Override
    protected String sign() {
        return "IS NOT";
    }

    public static NNIL NNIL(String columnName) {
        return new NNIL(columnName);
    }
}
