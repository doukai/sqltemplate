package io.sqltemplate.active.record.model.expression;

import io.sqltemplate.core.template.Parameter;

public interface Expression {

    static Object of(Object object) {
        if (object instanceof Expression) {
            return object;
        } else {
            return new Parameter(object);
        }
    }
}
