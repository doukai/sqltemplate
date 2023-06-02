package io.sqltemplate.active.record.model.join;

import java.util.ArrayList;
import java.util.List;

public class JoinTable {

    private String name;

    private final List<JoinColumn> joinColumns = new ArrayList<>();

    private final List<JoinColumn> inverseJoinColumns = new ArrayList<>();

    public JoinTable() {
    }

    public JoinTable setName(String name) {
        this.name = name;
        return this;
    }

    public JoinTable addJoinColumn(String name, String referencedColumnName) {
        this.joinColumns.add(new JoinColumn(name, referencedColumnName));
        return this;
    }

    public JoinTable addJoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.joinColumns.add(new JoinColumn(joinColumn.name(), joinColumn.referencedColumnName()));
        return this;
    }

    public JoinTable addReverseJoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.joinColumns.add(new JoinColumn(joinColumn.referencedColumnName(), joinColumn.name()));
        return this;
    }

    public JoinTable addInverseJoinColumn(String name, String referencedColumnName) {
        this.inverseJoinColumns.add(new JoinColumn(name, referencedColumnName));
        return this;
    }

    public JoinTable addInverseJoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.inverseJoinColumns.add(new JoinColumn(joinColumn.name(), joinColumn.referencedColumnName()));
        return this;
    }

    public JoinTable addReverseInverseJoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.inverseJoinColumns.add(new JoinColumn(joinColumn.referencedColumnName(), joinColumn.name()));
        return this;
    }

    public String getName() {
        return name;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public List<JoinColumn> getInverseJoinColumns() {
        return inverseJoinColumns;
    }
}
