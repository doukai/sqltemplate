package io.sqltemplate.active.record.model.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum Function implements Value {

    LAST_INSERT_ID;

    private List<Value> arguments;

    Function(Value... arguments) {
        this.arguments = Arrays.asList(arguments);
    }

    Function(Collection<Value> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public String toString() {
        return name() + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
