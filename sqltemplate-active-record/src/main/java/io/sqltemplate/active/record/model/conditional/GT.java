package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class GT extends Compare {

    public GT(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return ">";
    }

    public static GT gt(String tableAlias, String columnName, Object parameter) {
        return new GT(tableAlias, columnName, new Parameter(parameter));
    }

    public static GT gt(String columnName, Object parameter) {
        return new GT(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
