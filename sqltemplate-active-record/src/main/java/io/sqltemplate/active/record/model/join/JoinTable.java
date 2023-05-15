package io.sqltemplate.active.record.model.join;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JoinTable {

    private String name;

    private JoinColumn joinColumn;

    private JoinColumn inverseJoinColumn;

    public JoinTable(String name, List<JoinColumn> joinColumns, List<JoinColumn> inverseJoinColumns) {
        this.name = name;
        this.joinColumn = joinColumns.get(0);
        this.inverseJoinColumn = inverseJoinColumns.get(0);
    }

    public JoinTable(jakarta.persistence.JoinTable joinTable) {
        this(joinTable.name(), Arrays.stream(joinTable.joinColumns()).map(JoinColumn::new).collect(Collectors.toList()), Arrays.stream(joinTable.inverseJoinColumns()).map(JoinColumn::new).collect(Collectors.toList()));
    }

    public String getName() {
        return name;
    }

    public JoinTable setName(String name) {
        this.name = name;
        return this;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public JoinTable setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public JoinColumn getInverseJoinColumn() {
        return inverseJoinColumn;
    }

    public JoinTable setInverseJoinColumn(JoinColumn inverseJoinColumn) {
        this.inverseJoinColumn = inverseJoinColumn;
        return this;
    }
}
