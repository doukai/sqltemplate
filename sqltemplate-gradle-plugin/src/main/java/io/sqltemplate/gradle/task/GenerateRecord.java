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
import java.util.*;
import java.util.stream.Collectors;

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
                List<Map<String, Object>> columnMapList = getColumnMapList(tableName);
                List<FieldSpec> fieldSpecList = generateColumns(columnMapList);
                String typeName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
                ClassName recordClassName = generatorConfig.getBuildReactive() ?
                        ClassName.get("io.sqltemplate.active.record", "ReactiveRecord") :
                        ClassName.get("io.sqltemplate.active.record", "Record");
                ParameterizedTypeName recordParameterizedTypeName = ParameterizedTypeName.get(recordClassName, ClassName.get(generatorConfig.getPackageName(), typeName));
                TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(typeName)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(recordParameterizedTypeName)
                        .addJavadoc(remarks)
                        .addFields(fieldSpecList)
                        .addMethod(getTableNameMethod(tableName))
                        .addMethod(getKeyNamesMethod(tableName))
                        .addMethod(getColumnNamesMethod(columnMapList));
                fieldSpecList.forEach(fieldSpec -> addGetterAndSetter(fieldSpec, typeBuilder, typeName));
                typeSpecList.add(typeBuilder.build());
            }
            return typeSpecList;
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected List<Map<String, Object>> getColumnMapList(String tableName) {
        try (ResultSet columns = databaseMetaData.getColumns(generatorConfig.getSchemaName(), null, tableName, null)) {
            List<Map<String, Object>> columnMapList = new ArrayList<>();
            while (columns.next()) {
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("COLUMN_NAME", columns.getString("COLUMN_NAME"));
                columnMap.put("DATA_TYPE", columns.getInt("DATA_TYPE"));
                columnMap.put("IS_NULLABLE", columns.getString("IS_NULLABLE"));
                columnMap.put("IS_AUTOINCREMENT", columns.getString("IS_AUTOINCREMENT"));
                columnMap.put("REMARKS", columns.getString("REMARKS"));
                columnMapList.add(columnMap);
            }
            return columnMapList;
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected List<FieldSpec> generateColumns(List<Map<String, Object>> columnMapList) {
        List<FieldSpec> fieldSpecList = new ArrayList<>();
        for (Map<String, Object> columnMap : columnMapList) {
            String columnName = (String) columnMap.get("COLUMN_NAME");
            int datatype = (int) columnMap.get("DATA_TYPE");
            String isNullable = (String) columnMap.get("IS_NULLABLE");
            String isAutoIncrement = (String) columnMap.get("IS_AUTOINCREMENT");
            String remarks = (String) columnMap.get("REMARKS");
            FieldSpec field = FieldSpec.builder(getClassName(JDBCType.valueOf(datatype)), CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName.toLowerCase()), Modifier.PRIVATE)
                    .addJavadoc(remarks)
                    .build();
            fieldSpecList.add(field);
        }
        return fieldSpecList;
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

    public void addGetterAndSetter(FieldSpec fieldSpec, TypeSpec.Builder classBuilder, String typeName) {
        addGetter(fieldSpec, classBuilder);
        addSetter(fieldSpec, classBuilder, typeName);
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

    private void addSetter(FieldSpec fieldSpec, TypeSpec.Builder classBuilder, String typeName) {
        String setterName = getFieldSetterMethodName(fieldSpec);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setterName).addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(fieldSpec.type, fieldSpec.name);
        methodBuilder.addStatement("this." + fieldSpec.name + " = " + fieldSpec.name);
        if (generatorConfig.getFluentSetter()) {
            methodBuilder.returns(ClassName.get(generatorConfig.getPackageName(), typeName));
            methodBuilder.addStatement("return this");
        }
        classBuilder.addMethod(methodBuilder.build());
    }

    public String getFieldSetterMethodName(FieldSpec fieldSpec) {
        return "set".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldSpec.name));
    }

    public MethodSpec getTableNameMethod(String tableName) {
        return MethodSpec.methodBuilder("getTableName").returns(String.class).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return $S", tableName)
                .build();
    }

    public MethodSpec getKeyNamesMethod(String tableName) {
        try (ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(generatorConfig.getSchemaName(), null, tableName)) {
            List<String> primaryKeyColumnNameList = new ArrayList<>();
            while (primaryKeys.next()) {
                String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                primaryKeyColumnNameList.add(primaryKeyColumnName);
            }
            return MethodSpec.methodBuilder("getKeyNames").returns(ArrayTypeName.of(String.class)).addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return new $T{$L}",
                            ArrayTypeName.of(String.class),
                            CodeBlock.join(primaryKeyColumnNameList.stream().map(name -> CodeBlock.of("$S", name)).collect(Collectors.toList()), ", ")
                    )
                    .build();
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    public MethodSpec getColumnNamesMethod(List<Map<String, Object>> columnMapList) {
        List<String> columnNameList = new ArrayList<>();
        for (Map<String, Object> columnMap : columnMapList) {
            String columnName = (String) columnMap.get("COLUMN_NAME");
            columnNameList.add(columnName);
        }
        return MethodSpec.methodBuilder("getColumnNames").returns(ArrayTypeName.of(String.class)).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return new $T{$L}",
                        ArrayTypeName.of(String.class),
                        CodeBlock.join(columnNameList.stream().map(name -> CodeBlock.of("$S", name)).collect(Collectors.toList()), ", ")
                )
                .build();
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
