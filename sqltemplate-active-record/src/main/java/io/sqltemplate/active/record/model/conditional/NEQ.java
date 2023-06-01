package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.core.utils.Parameter;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NEQ extends Compare {

    public NEQ(String tableAlias, String columnName, Parameter parameter) {
        super(tableAlias, columnName, parameter);
    }

    @Override
    protected String sign() {
        return "<>";
    }

    public static NEQ neq(String tableAlias, String columnName, Object parameter) {
        return new NEQ(tableAlias, columnName, new Parameter(parameter)));
    }

    public static NEQ neq(String columnName, Object parameter) {
        return new NEQ(DEFAULT_ALIAS, columnName, new Parameter(parameter));
    }
}
