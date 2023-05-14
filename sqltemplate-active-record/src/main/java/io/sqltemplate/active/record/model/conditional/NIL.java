package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.NullValue;

public class NIL extends Compare {

    public NIL(String columnName) {
        super(columnName, new NullValue());
    }

    @Override
    protected String sign() {
        return "IS";
    }

    public static NIL NIL(String columnName) {
        return new NIL(columnName);
    }
}
