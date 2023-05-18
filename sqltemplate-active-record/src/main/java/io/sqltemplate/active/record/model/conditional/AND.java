package io.sqltemplate.active.record.model.conditional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class AND implements Conditional {

    private Collection<Conditional> conditionals;

    public AND(Collection<Conditional> conditionals) {
        this.conditionals = conditionals;
    }

    public AND addConditional(Conditional conditional) {
        if (this.conditionals == null) {
            this.conditionals = new ArrayList<>();
        }
        this.conditionals.add(conditional);
        return this;
    }

    public static AND and(Collection<Conditional> conditionals) {
        return new AND(conditionals);
    }

    public static AND and(Conditional... conditionals) {
        return new AND(Arrays.stream(conditionals).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return conditionals.stream().map(Object::toString).collect(Collectors.joining(" AND ", "(", ")"));
    }
}
