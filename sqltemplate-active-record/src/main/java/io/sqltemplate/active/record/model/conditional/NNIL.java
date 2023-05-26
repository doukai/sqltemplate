package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.expression.NullValue;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NNIL extends Compare {

    public NNIL(String tableAlias, String columnName) {
        super(tableAlias, columnName, new NullValue());
    }

    @Override
    protected String sign() {
        return "IS NOT";
    }

    public static NNIL nnil(String tableAlias, String columnName) {
        return new NNIL(tableAlias, columnName);
    }

    public static NNIL nnil(String columnName) {
        return new NNIL(DEFAULT_ALIAS, columnName);
    }
}
