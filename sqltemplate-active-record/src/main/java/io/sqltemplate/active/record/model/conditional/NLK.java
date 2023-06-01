package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NLK extends Compare {

    public NLK(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return "NOT LIKE";
    }

    public static NLK nlk(String tableAlias, String columnName, Object parameter) {
        return new NLK(tableAlias, columnName, new Parameter(parameter));
    }

    public static NLK nlk(String columnName, Object parameter) {
        return new NLK(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
