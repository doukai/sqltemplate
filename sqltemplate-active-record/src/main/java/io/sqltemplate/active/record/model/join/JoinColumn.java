package io.sqltemplate.active.record.model.join;

import com.google.common.base.CaseFormat;

public class JoinColumn {

    private String name;

    private String referencedColumnName;

    public JoinColumn(String name, String referencedColumnName) {
        this.name = name;
        this.referencedColumnName = referencedColumnName;
    }

    public String getName() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    public JoinColumn setName(String name) {
        this.name = name;
        return this;
    }

    public String getReferencedColumnName() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, referencedColumnName);
    }

    public JoinColumn setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
        return this;
    }
}
