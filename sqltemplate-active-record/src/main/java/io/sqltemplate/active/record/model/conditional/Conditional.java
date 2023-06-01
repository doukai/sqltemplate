package io.sqltemplate.active.record.model.conditional;

public interface Conditional {

    default boolean isAnd() {
        return false;
    }

    default boolean isOr() {
        return false;
    }

    default boolean isCompare() {
        return false;
    }
}
