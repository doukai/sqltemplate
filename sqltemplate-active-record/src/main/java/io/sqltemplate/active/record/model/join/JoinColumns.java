package io.sqltemplate.active.record.model.join;

import java.util.List;

public class JoinColumns {

    private List<JoinColumn> joinColumns;

    public JoinColumns() {
    }

    public JoinColumns addJoinColumn(String name, String referencedColumnName) {
        this.joinColumns.add(new JoinColumn(name, referencedColumnName));
        return this;
    }

    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }
}
