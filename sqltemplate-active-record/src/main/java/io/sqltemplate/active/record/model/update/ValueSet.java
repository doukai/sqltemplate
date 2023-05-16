package io.sqltemplate.active.record.model.update;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.expression.Expression;

public class ValueSet {
    private final String columnName;
    private final Expression expression;

    public ValueSet(String columnName, Expression expression) {
        this.columnName = columnName;
        this.expression = expression;
    }

    public static ValueSet SET(String columnName, Object value) {
        return new ValueSet(columnName, Expression.of(value));
    }

    @Override
    public String toString() {
        return "t." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " = " + expression;
    }
}
