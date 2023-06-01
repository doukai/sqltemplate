package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class EQ extends Compare {

    public EQ(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return "=";
    }

    public static EQ eq(String tableAlias, String columnName, Object parameter) {
        return new EQ(tableAlias, columnName, new Parameter(parameter));
    }

    public static EQ eq(String columnName, Object parameter) {
        return new EQ(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
