package io.sqltemplate.core.expression;

public interface Expression {

    static Expression of(Object object) {
        if (object instanceof Expression) {
            return (Expression) object;
        }
        return new Parameter(object);
    }
}
