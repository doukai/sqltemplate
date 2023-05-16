package io.sqltemplate.active.record.model.conditional;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.expression.Expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class IN implements Conditional {

    private final String columnName;
    private final Collection<Expression> expressions;

    public IN(String columnName, Collection<Expression> expressions) {
        this.columnName = columnName;
        this.expressions = expressions;
    }

    public static IN IN(String columnName, Collection<Object> expressions) {
        return new IN(columnName, expressions.stream().map(Expression::of).collect(Collectors.toList()));
    }

    public static IN IN(String columnName, Object... expressions) {
        return new IN(columnName, Arrays.stream(expressions).map(Expression::of).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "t." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " IN " + expressions.stream().map(Object::toString).collect(Collectors.joining(", ", "(", ")"));
    }
}
