package io.sqltemplate.active.record.model.join;

public class JoinColumn {

    private String name;

    private String referencedColumnName;

    public JoinColumn(String name, String referencedColumnName) {
        this.name = name;
        this.referencedColumnName = referencedColumnName;
    }

    public JoinColumn(jakarta.persistence.JoinColumn joinColumn) {
        this.name = joinColumn.name();
        this.referencedColumnName = joinColumn.referencedColumnName();
    }

    public String getName() {
        return name;
    }

    public JoinColumn setName(String name) {
        this.name = name;
        return this;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public JoinColumn setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
        return this;
    }
}
