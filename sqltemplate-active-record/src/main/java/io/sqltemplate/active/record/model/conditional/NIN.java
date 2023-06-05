package io.sqltemplate.active.record.model.conditional;

import io.sqltemplate.active.record.model.expression.Expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.TableRecord.DEFAULT_ALIAS;

public class NIN extends Compare {

    public NIN(String tableAlias, String columnName, List<Object> expressions) {
        super(tableAlias, columnName, expressions);
    }

    @Override
    public String getSign() {
        return "NOT IN";
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
}
