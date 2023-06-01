package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class LTE extends Compare {

    public LTE(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return "<=";
    }

    public static LTE lte(String tableAlias, String columnName, Object parameter) {
        return new LTE(tableAlias, columnName, new Parameter(parameter));
    }

    public static LTE lte(String columnName, Object parameter) {
        return new LTE(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
