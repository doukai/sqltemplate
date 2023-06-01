package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class LT extends Compare {

    public LT(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return "<";
    }

    public static LT lt(String tableAlias, String columnName, Object parameter) {
        return new LT(tableAlias, columnName, new Parameter(parameter));
    }

    public static LT lt(String columnName, Object parameter) {
        return new LT(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
