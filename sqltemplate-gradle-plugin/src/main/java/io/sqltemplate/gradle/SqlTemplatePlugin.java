package io.sqltemplate.gradle;

import io.sqltemplate.gradle.config.GeneratorConfig;
import io.sqltemplate.gradle.config.JDBCConfig;
import io.sqltemplate.gradle.task.GenerateRecord;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SqlTemplatePlugin implements Plugin<Project> {
    private static final String GROUP_NAME = "sqltemplate";

    @Override
    public void apply(Project project) {
        project.getExtensions().create("jdbc", JDBCConfig.class);
        project.getExtensions().create("generator", GeneratorConfig.class);
        project.getTasks().create("generateRecord", GenerateRecord.class).setGroup(GROUP_NAME);
    }
}
