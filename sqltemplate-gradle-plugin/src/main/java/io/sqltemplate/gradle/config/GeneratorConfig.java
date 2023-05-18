package io.sqltemplate.gradle.config;

public class GeneratorConfig {

    private String schemaName;

    private String packageName;

    private Boolean fluentSetter = true;

    private Boolean buildReactive = false;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getFluentSetter() {
        return fluentSetter;
    }

    public void setFluentSetter(Boolean fluentSetter) {
        this.fluentSetter = fluentSetter;
    }

    public Boolean getBuildReactive() {
        return buildReactive;
    }

    public void setBuildReactive(Boolean buildReactive) {
        this.buildReactive = buildReactive;
    }
}
