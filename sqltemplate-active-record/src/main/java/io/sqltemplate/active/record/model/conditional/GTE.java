package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class GTE extends Compare {

    public GTE(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return ">=";
    }

    public static GTE gte(String tableAlias, String columnName, Object parameter) {
        return new GTE(tableAlias, columnName, new Parameter(parameter));
    }

    public static GTE gte(String columnName, Object parameter) {
        return new GTE(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
