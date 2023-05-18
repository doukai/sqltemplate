package io.sqltemplate.gradle.config;

public class GeneratorConfig {

    private String packageName;
    private Boolean buildReactive = false;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getBuildReactive() {
        return buildReactive;
    }

    public void setBuildReactive(Boolean buildReactive) {
        this.buildReactive = buildReactive;
    }
}
