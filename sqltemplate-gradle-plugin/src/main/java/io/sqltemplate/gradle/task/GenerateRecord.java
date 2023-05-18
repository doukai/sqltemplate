package io.sqltemplate.gradle.task;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.sqltemplate.gradle.config.GeneratorConfig;
import io.sqltemplate.gradle.config.JDBCConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenerateRecord extends DefaultTask {

    protected static final String MAIN_PATH = "src".concat(File.separator).concat("main");
    protected static final String MAIN_JAVA_PATH = MAIN_PATH.concat(File.separator).concat("java");
    protected static final String MAIN_RESOURCES_PATH = MAIN_PATH.concat(File.separator).concat("resources");
    private DatabaseMetaData databaseMetaData;
    private GeneratorConfig generatorConfig;

    @TaskAction
    public void generateRecord() {
        SourceSet sourceSet = getProject().getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        String javaPath = sourceSet.getJava().getSourceDirectories().filter(file -> file.getPath().contains(MAIN_JAVA_PATH)).getAsPath();
        generatorConfig = getProject().getExtensions().findByType(GeneratorConfig.class);
        try (Connection connection = createConnection()) {
            databaseMetaData = connection.getMetaData();
            for (TypeSpec typeSpec : generateTables()) {
                JavaFile.builder(Objects.requireNonNull(generatorConfig).getPackageName(), typeSpec).build().writeTo(new File(javaPath));
            }
        } catch (IOException | SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected List<TypeSpec> generateTables() {
        try (ResultSet tables = databaseMetaData.getTables(generatorConfig.getSchemaName(), null, null, new String[]{"TABLE"})) {
            List<TypeSpec> typeSpecList = new ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String remarks = tables.getString("REMARKS");
                List<FieldSpec> fieldSpecList = generateColumns(tableName);
                TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase()))
                        .addModifiers(Modifier.PUBLIC)
                        .addJavadoc(remarks)
                        .addFields(fieldSpecList);
                fieldSpecList.forEach(fieldSpec -> addGetterAndSetter(fieldSpec, typeBuilder));
                typeSpecList.add(typeBuilder.build());
            }
            return typeSpecList;
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected List<FieldSpec> generateColumns(String tableName) {
        try (ResultSet columns = databaseMetaData.getColumns(generatorConfig.getSchemaName(), null, tableName, null)) {
            List<FieldSpec> fieldSpecList = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int datatype = columns.getInt("DATA_TYPE");
                String isNullable = columns.getString("IS_NULLABLE");
                String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                String remarks = columns.getString("REMARKS");
                FieldSpec field = FieldSpec.builder(getClassName(JDBCType.valueOf(datatype)), CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName.toLowerCase()), Modifier.PRIVATE)
                        .addJavadoc(remarks)
                        .build();
                fieldSpecList.add(field);

            }
            return fieldSpecList;
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected void generatePrimaryKeys(String tableName) {
        try (ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(generatorConfig.getSchemaName(), null, tableName)) {
            while (primaryKeys.next()) {
                String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");

            }
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    ClassName getClassName(JDBCType jdbcType) {
        switch (jdbcType) {
            case BIT:
            case TINYINT:
                return ClassName.get(Boolean.class);
            case SMALLINT:
                return ClassName.get(Short.class);
            case INTEGER:
                return ClassName.get(Integer.class);
            case BIGINT:
                return ClassName.get(BigInteger.class);
            case FLOAT:
                return ClassName.get(Float.class);
            case REAL:
            case DOUBLE:
                return ClassName.get(Double.class);
            case NUMERIC:
                return ClassName.get(Number.class);
            case DECIMAL:
                return ClassName.get(BigDecimal.class);
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
                return ClassName.get(String.class);
            case DATE:
                return ClassName.get(LocalDate.class);
            case TIME:
                return ClassName.get(LocalTime.class);
            case TIMESTAMP:
                return ClassName.get(LocalDateTime.class);
            default:
                return ClassName.get(Object.class);
        }
    }

    public void addGetterAndSetter(FieldSpec fieldSpec, TypeSpec.Builder classBuilder) {
        addGetter(fieldSpec, classBuilder);
        addSetter(fieldSpec, classBuilder);
    }

    private void addSetter(FieldSpec fieldSpec, TypeSpec.Builder classBuilder) {
        String setterName = getFieldSetterMethodName(fieldSpec);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setterName).addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(fieldSpec.type, fieldSpec.name);
        methodBuilder.addStatement("this." + fieldSpec.name + " = " + fieldSpec.name);
        classBuilder.addMethod(methodBuilder.build());
    }

    public void addGetter(FieldSpec fieldSpec, TypeSpec.Builder classBuilder) {
        String getterName = getFieldGetterMethodName(fieldSpec);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(getterName).returns(fieldSpec.type).addModifiers(Modifier.PUBLIC);
        methodBuilder.addStatement("return this." + fieldSpec.name);
        classBuilder.addMethod(methodBuilder.build());
    }

    public String getFieldGetterMethodName(FieldSpec fieldSpec) {
        return "get".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldSpec.name));
    }

    public String getFieldSetterMethodName(FieldSpec fieldSpec) {
        return "set".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldSpec.name));
    }

    protected Connection createConnection() {
        JDBCConfig jdbcConfig = getProject().getExtensions().findByType(JDBCConfig.class);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Objects.requireNonNull(jdbcConfig).getJdbcUrl());
        config.setUsername(jdbcConfig.getUsername());
        config.setPassword(jdbcConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", jdbcConfig.getCachePrepStmts());
        config.addDataSourceProperty("prepStmtCacheSize", jdbcConfig.getPrepStmtCacheSize());
        config.addDataSourceProperty("prepStmtCacheSqlLimit", jdbcConfig.getPrepStmtCacheSqlLimit());
        config.addDataSourceProperty("useServerPrepStmts", jdbcConfig.getUseServerPrepStmts());
        config.addDataSourceProperty("useLocalSessionState", jdbcConfig.getUseLocalSessionState());
        config.addDataSourceProperty("rewriteBatchedStatements", jdbcConfig.getRewriteBatchedStatements());
        config.addDataSourceProperty("cacheResultSetMetadata", jdbcConfig.getCacheResultSetMetadata());
        config.addDataSourceProperty("cacheServerConfiguration", jdbcConfig.getCacheServerConfiguration());
        config.addDataSourceProperty("elideSetAutoCommits", jdbcConfig.getElideSetAutoCommits());
        config.addDataSourceProperty("maintainTimeStats", jdbcConfig.getMaintainTimeStats());
        HikariDataSource ds = new HikariDataSource(config);
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }
}
