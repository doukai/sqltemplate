package io.sqltemplate.active.record.model.conditional;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.expression.Expression;

public abstract class Compare implements Conditional {

    private final String tableAlias;
    private final String columnName;
    private final Expression expression;

    public Compare(String tableAlias, String columnName, Expression expression) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expression = expression;
    }

    protected abstract String sign();

    @Override
    public String toString() {
        return tableAlias + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " " + sign() + " " + expression;
    }
}
