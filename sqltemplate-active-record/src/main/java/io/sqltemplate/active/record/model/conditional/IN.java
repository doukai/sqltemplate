package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class IN extends Compare {

    public IN(String tableAlias, String columnName, List<Object> expressions) {
        super(tableAlias, columnName, expressions);
    }

    @Override
    public String getSign() {
        return "IN";
    }

    public static IN in(String tableAlias, String columnName, Collection<Object> expressions) {
        return new IN(tableAlias, columnName, expressions.stream().map(Expression::of).collect(Collectors.toList()));
    }

    public static IN in(String tableAlias, String columnName, Object... expressions) {
        return new IN(tableAlias, columnName, Arrays.stream(expressions).map(Expression::of).collect(Collectors.toList()));
    }

    public static IN in(String columnName, Collection<Object> expressions) {
        return new IN(DEFAULT_ALIAS, columnName, expressions.stream().map(Expression::of).collect(Collectors.toList()));
    }

    public static IN in(String columnName, Object... expressions) {
        return new IN(DEFAULT_ALIAS, columnName, Arrays.stream(expressions).map(Expression::of).collect(Collectors.toList()));
    }
}
