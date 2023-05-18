package io.sqltemplate.active.record.model.conditional;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.expression.Expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NIN implements Conditional {

    private final String tableAlias;
    private final String columnName;
    private final Collection<Expression> expressions;

    public NIN(String tableAlias, String columnName, Collection<Expression> expressions) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.expressions = expressions;
    }

    public static NIN nin(String tableAlias, String columnName, Collection<Object> expressions) {
        return new NIN(tableAlias, columnName, expressions.stream().map(Expression::of).collect(Collectors.toList()));
    }

    public static NIN nin(String tableAlias, String columnName, Object... expressions) {
        return new NIN(tableAlias, columnName, Arrays.stream(expressions).map(Expression::of).collect(Collectors.toList()));
    }

    public static NIN nin(String columnName, Collection<Object> expressions) {
        return new NIN(DEFAULT_ALIAS, columnName, expressions.stream().map(Expression::of).collect(Collectors.toList()));
    }

    public static NIN nin(String columnName, Object... expressions) {
        return new NIN(DEFAULT_ALIAS, columnName, Arrays.stream(expressions).map(Expression::of).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return tableAlias + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName) + " NOT IN " + expressions.stream().map(Object::toString).collect(Collectors.joining(", ", "(", ")"));
    }
}
