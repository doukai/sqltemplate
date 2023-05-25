package io.sqltemplate.core.expression;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface Expression {

    static Object of(Object object) {
        if (object == null) {
            return new NullValue();
        } else if (object instanceof Number) {
            return new NumberValue((Number) object);
        } else if (object instanceof Enum) {
            return new StringValue(((Enum<?>) object).name());
        } else if (object instanceof LocalDate) {
            return new StringValue((LocalDate) object);
        } else if (object instanceof LocalTime) {
            return new StringValue((LocalTime) object);
        } else if (object instanceof LocalDateTime) {
            return new StringValue((LocalDateTime) object);
        } else {
            return object;
        }
    }
}
