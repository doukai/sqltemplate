package io.sqltemplate.active.record.model.expression;

public interface Value {

    static Value of(Object object) {
        if (object == null) {
            return new NullValue();
        } else if (object instanceof Value) {
            return (Value) object;
        } else if (object instanceof Number) {
            return new NumberValue((Number) object);
        } else if (object instanceof Enum) {
            return new StringValue(((Enum<?>) object).name());
        } else {
            return new StringValue(object);
        }
    }
}
