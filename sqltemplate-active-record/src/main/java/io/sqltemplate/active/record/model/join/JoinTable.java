package io.sqltemplate.active.record.model.join;

import java.util.List;

public class JoinTable {

    private String name;

    private List<JoinColumn> joinColumns;

    private List<JoinColumn> inverseJoinColumns;

    public JoinTable(String name, List<JoinColumn> joinColumns, List<JoinColumn> inverseJoinColumns) {
        this.name = name;
        this.joinColumns = joinColumns;
        this.inverseJoinColumns = inverseJoinColumns;
    }

    public String getName() {
        return name;
    }

    public JoinTable setName(String name) {
        this.name = name;
        return this;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(List<JoinColumn> joinColumns) {
        this.joinColumns = joinColumns;
    }

    public List<JoinColumn> getInverseJoinColumns() {
        return inverseJoinColumns;
    }

    public void setInverseJoinColumns(List<JoinColumn> inverseJoinColumns) {
        this.inverseJoinColumns = inverseJoinColumns;
    }
}
