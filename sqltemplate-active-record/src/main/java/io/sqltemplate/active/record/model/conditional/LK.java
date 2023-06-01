package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class LK extends Compare {

    public LK(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return "LIKE";
    }

    public static LK lk(String tableAlias, String columnName, Object parameter) {
        return new LK(tableAlias, columnName, new Parameter(parameter));
    }

    public static LK lk(String columnName, Object parameter) {
        return new LK(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
