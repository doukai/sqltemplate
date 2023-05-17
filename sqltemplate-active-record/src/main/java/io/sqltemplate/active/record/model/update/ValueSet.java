package io.sqltemplate.active.record.model.update;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.expression.Expression;

public class ValueSet {
    private final String tableAlias;
    private final String columnName;
    private final Expression expression;

    public ValueSet(String tableAlias, String columnName, Expression expression) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expression = expression;
    }

    public static ValueSet SET(String tableAlias, String columnName, Object value) {
        return new ValueSet(tableAlias, columnName, Expression.of(value));
    }

    @Override
    public String toString() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " = " + expression;
    }
}
