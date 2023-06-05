package io.sqltemplate.active.record.model.conditional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class OR implements Conditional {

    private Collection<Conditional> conditionals;

    public OR(Collection<Conditional> conditionals) {
        this.conditionals = conditionals;
    }

    public OR addConditional(Conditional conditional) {
        if (this.conditionals == null) {
            this.conditionals = new ArrayList<>();
        }
        this.conditionals.add(conditional);
        return this;
    }

    public Collection<Conditional> getConditionals() {
        return conditionals;
    }

    public static OR or(Collection<Conditional> conditionals) {
        return new OR(conditionals);
    }

    public static OR or(Conditional... conditionals) {
        return new OR(Arrays.stream(conditionals).collect(Collectors.toList()));
    }

    @Override
    public boolean isOr() {
        return true;
    }
}
