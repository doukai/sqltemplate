package io.sqltemplate.active.record.model.join;

import java.util.List;

public class JoinTable {

    private String name;

    private List<JoinColumn> joinColumns;

    private List<JoinColumn> inverseJoinColumns;

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

    public JoinTable addInverseJoinColumn(String name, String referencedColumnName) {
        this.inverseJoinColumns.add(new JoinColumn(name, referencedColumnName));
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
