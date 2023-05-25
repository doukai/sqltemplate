package io.sqltemplate.core.expression;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class StringValue implements Expression {

    private final String value;

    public StringValue(LocalDate value) {
        this.value = Date.valueOf(value).toString();
    }

    public StringValue(LocalTime value) {
        this.value = Time.valueOf(value).toString();
    }

    public StringValue(LocalDateTime value) {
        this.value = Timestamp.valueOf(value).toString();
    }

    public StringValue(Object value) {
        this.value = value.toString();
    }

    @Override
    public String toString() {
        return (value.trim().startsWith("'") ? "" : "'") + value.trim() + (value.trim().endsWith("'") ? "" : "'");
    }
}
