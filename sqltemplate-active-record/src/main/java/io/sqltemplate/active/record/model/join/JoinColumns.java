package io.sqltemplate.active.record.model.join;

import java.util.ArrayList;
import java.util.List;

public class JoinColumns {

    private final List<JoinColumn> joinColumns = new ArrayList<>();

    public JoinColumns() {
    }

    public JoinColumns addJoinColumn(String name, String referencedColumnName) {
        this.joinColumns.add(new JoinColumn(name, referencedColumnName));
        return this;
    }

    public JoinColumns addJoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.joinColumns.add(new JoinColumn(joinColumn.name(), joinColumn.referencedColumnName()));
        return this;
    }

    public JoinColumns addReverseJoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.joinColumns.add(new JoinColumn(joinColumn.referencedColumnName(), joinColumn.name()));
        return this;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }
}
