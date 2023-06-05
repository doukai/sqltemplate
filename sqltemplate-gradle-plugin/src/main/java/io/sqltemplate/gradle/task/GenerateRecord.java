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
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;
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
        String resourcePath = sourceSet.getResources().getSourceDirectories().filter(file -> file.getPath().contains(MAIN_RESOURCES_PATH)).getAsPath();
        String javaPath = sourceSet.getJava().getSourceDirectories().filter(file -> file.getPath().contains(MAIN_JAVA_PATH)).getAsPath();
        generatorConfig = getProject().getExtensions().findByType(GeneratorConfig.class);
        try (Connection connection = createConnection()) {
            databaseMetaData = connection.getMetaData();
            List<Map<String, Object>> tableMapList = buildTableMapList();
            for (TypeSpec typeSpec : generateTables(tableMapList, generatorConfig.getBuildReactive())) {
                JavaFile.builder(Objects.requireNonNull(generatorConfig).getPackageName(), typeSpec).build().writeTo(new File(javaPath));
            }
            JavaFile.builder(Objects.requireNonNull(generatorConfig).getPackageName(), generateTableRecordIndex(tableMapList)).build().writeTo(new File(javaPath));
            Path filePath = Paths.get(resourcePath).resolve("META-INF").resolve("services");
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath);
            }
            try (PrintWriter out = new PrintWriter(filePath.resolve("io.sqltemplate.active.record.RecordIndex").toFile())) {
                out.println(Objects.requireNonNull(generatorConfig).getPackageName() + ".TableRecordIndex");
            }
        } catch (IOException | SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected List<Map<String, Object>> buildTableMapList() {
        try (ResultSet tables = databaseMetaData.getTables(generatorConfig.getSchemaName(), null, null, new String[]{"TABLE"})) {
            List<Map<String, Object>> tableMapList = new ArrayList<>();
            while (tables.next()) {
                Map<String, Object> tableMap = new HashMap<>();
                tableMap.put("TABLE_NAME", tables.getString("TABLE_NAME"));
                tableMap.put("REMARKS", tables.getString("REMARKS"));
                tableMapList.add(tableMap);
            }
            return tableMapList;
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    protected List<TypeSpec> generateTables(List<Map<String, Object>> tableMapList, boolean reactive) {
        List<TypeSpec> typeSpecList = new ArrayList<>();
        for (Map<String, Object> tableMap : tableMapList) {
            String tableName = (String) tableMap.get("TABLE_NAME");
            String remarks = (String) tableMap.get("REMARKS");
            List<Map<String, Object>> columnMapList = getColumnMapList(tableName);
            List<FieldSpec> fieldSpecList = generateColumns(columnMapList);
            String typeName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
            ClassName recordClassName = reactive ?
                    ClassName.get("io.sqltemplate.active.record", "ReactiveRecord") :
                    ClassName.get("io.sqltemplate.active.record", "Record");
            ClassName className = ClassName.get(generatorConfig.getPackageName(), typeName);
            ParameterizedTypeName recordParameterizedTypeName = ParameterizedTypeName.get(recordClassName, className);
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(typeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpec.builder(ClassName.get("jakarta.annotation", "Generated")).addMember("value", "$S", GenerateRecord.class.getCanonicalName()).build())
                    .addAnnotation(AnnotationSpec.builder(ClassName.get("jakarta.persistence", "Table")).addMember("name", "$S", tableName).build())
                    .superclass(recordParameterizedTypeName)
                    .addJavadoc(remarks)
                    .addFields(fieldSpecList)
                    .addField(getTableNameField(tableName))
                    .addField(getKeyNamesField(tableName))
                    .addField(getColumnNamesField(columnMapList))
                    .addField(isAutoIncrementField(columnMapList))
                    .addMethod(buildTableNameMethod())
                    .addMethod(buildKeyNamesMethod())
                    .addMethod(buildColumnNamesMethod())
                    .addMethod(buildValueMethod(columnMapList))
                    .addMethod(mapToEntityMethod(typeName, columnMapList))
                    .addMethod(isAutoIncrementMethod())
                    .addMethod(buildGetMethod(className, reactive))
                    .addMethod(buildAllMethod(className, reactive))
                    .addMethod(buildFirstOfAllMethod(className, reactive))
                    .addMethod(buildLastOfAllMethod(className, reactive))
                    .addMethod(buildAllCountMethod(reactive))
                    .addMethod(buildInsertAllMethod(className, reactive))
                    .addMethod(buildUpdateAllMethod(className, reactive))
                    .addMethod(buildDeleteAllMethod(className, reactive))
                    .addMethod(buildWhereMethod(className, reactive))
                    .addMethod(buildWhereConditionalMethod(className, reactive))
                    .addMethod(buildWhereConditionalsMethod(className, reactive))
                    .addMethod(buildRecordMethod(className));
            fieldSpecList.forEach(fieldSpec -> addGetterAndSetter(fieldSpec, typeBuilder, typeName));
            typeSpecList.add(typeBuilder.build());
        }
        return typeSpecList;
    }

    public MethodSpec buildGetMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), className) : className;
        return MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .varargs(true)
                .addParameter(ParameterSpec.builder(ArrayTypeName.of(Object.class), "values").build())
                .returns(returnTypeName)
                .addStatement("return get(tableName, values)")
                .build();
    }

    public MethodSpec buildAllMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ?
                ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), ParameterizedTypeName.get(ClassName.get(List.class), className)) :
                ParameterizedTypeName.get(ClassName.get(List.class), className);
        return MethodSpec.methodBuilder("all")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnTypeName)
                .addStatement("return all(tableName)")
                .build();
    }

    public MethodSpec buildFirstOfAllMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), className) : className;
        return MethodSpec.methodBuilder("firstOfAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnTypeName)
                .addStatement("return firstOfAll(tableName)")
                .build();
    }

    public MethodSpec buildLastOfAllMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), className) : className;
        return MethodSpec.methodBuilder("lastOfAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .varargs(true)
                .addParameter(ParameterSpec.builder(ArrayTypeName.of(Object.class), "columnNames").build())
                .returns(returnTypeName)
                .addStatement("return lastOfAll(tableName, columnNames)")
                .build();
    }

    public MethodSpec buildAllCountMethod(boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), ClassName.get(Long.class)) : TypeName.LONG;
        return MethodSpec.methodBuilder("allCount")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnTypeName)
                .addStatement("return allCount(tableName)")
                .build();
    }

    public MethodSpec buildInsertAllMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ?
                ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), ParameterizedTypeName.get(ClassName.get(List.class), className)) :
                ParameterizedTypeName.get(ClassName.get(List.class), className);
        return MethodSpec.methodBuilder("insertAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .varargs(true)
                .addParameter(ParameterSpec.builder(ArrayTypeName.of(className), "records").build())
                .returns(returnTypeName)
                .addStatement("return insertAll(tableName, records)")
                .build();
    }

    public MethodSpec buildUpdateAllMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ?
                ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), ParameterizedTypeName.get(ClassName.get(List.class), className)) :
                ParameterizedTypeName.get(ClassName.get(List.class), className);
        return MethodSpec.methodBuilder("updateAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .varargs(true)
                .addParameter(ParameterSpec.builder(ArrayTypeName.of(className), "records").build())
                .returns(returnTypeName)
                .addStatement("return updateAll(tableName, records)")
                .build();
    }

    public MethodSpec buildDeleteAllMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), ClassName.get(Long.class)) : TypeName.LONG;
        return MethodSpec.methodBuilder("deleteAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .varargs(true)
                .addParameter(ParameterSpec.builder(ArrayTypeName.of(className), "records").build())
                .returns(returnTypeName)
                .addStatement("return deleteAll(tableName, records)")
                .build();
    }

    public MethodSpec buildWhereMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), className) : className;
        return MethodSpec.methodBuilder("where")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnTypeName)
                .addStatement("return where(tableName)")
                .build();
    }

    public MethodSpec buildWhereConditionalMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), className) : className;
        return MethodSpec.methodBuilder("where")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(ClassName.get("io.sqltemplate.active.record.model.conditional", "Conditional"), "conditional").build())
                .returns(returnTypeName)
                .addStatement("return where(tableName, conditional)")
                .build();
    }

    public MethodSpec buildWhereConditionalsMethod(ClassName className, boolean reactive) {
        TypeName returnTypeName = reactive ? ParameterizedTypeName.get(ClassName.get("reactor.core.publisher", "Mono"), className) : className;
        return MethodSpec.methodBuilder("where")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .varargs(true)
                .addParameter(ParameterSpec.builder(ArrayTypeName.of(ClassName.get("io.sqltemplate.active.record.model.conditional", "Conditional")), "conditionals").build())
                .returns(returnTypeName)
                .addStatement("return where(tableName, conditionals)")
                .build();
    }

    public MethodSpec buildRecordMethod(ClassName className) {
        return MethodSpec.methodBuilder("record")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(className)
                .addStatement("return record(tableName)")
                .build();
    }

    protected TypeSpec generateTableRecordIndex(List<Map<String, Object>> tableMapList) {
        return TypeSpec.classBuilder("TableRecordIndex")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("io.sqltemplate.active.record", "RecordIndex"))
                .addStaticBlock(getRegisterEntityClassCodeBlock(tableMapList))
                .addMethod(getRecordSupplierMethod(tableMapList))
                .build();
    }

    public CodeBlock getRegisterEntityClassCodeBlock(List<Map<String, Object>> tableMapList) {
        return CodeBlock.join(
                tableMapList.stream()
                        .map(tableMap ->
                                CodeBlock.of("$T.registerEntityClass($T.class);\r\n",
                                        ClassName.get("io.sqltemplate.active.record", "TableRecord"),
                                        ClassName.get(generatorConfig.getPackageName(), CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, ((String) tableMap.get("TABLE_NAME")).toLowerCase()))
                                )
                        )
                        .collect(Collectors.toList()),
                ""
        );
    }

    public MethodSpec getRecordSupplierMethod(List<Map<String, Object>> tableMapList) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getRecordSupplier")
                .returns(ParameterizedTypeName.get(ClassName.get(Supplier.class), ParameterizedTypeName.get(ClassName.get("io.sqltemplate.active.record", "TableRecord"), TypeVariableName.get("?"))))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(String.class, "tableName").build());

        int index = 0;
        for (Map<String, Object> tableMap : tableMapList) {
            String tableName = (String) tableMap.get("TABLE_NAME");
            String typeName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
            if (index == 0) {
                builder.beginControlFlow("if (tableName.equals($S))", tableName);
            } else {
                builder.nextControlFlow("else if (tableName.equals($S))", tableName);
            }
            builder.addStatement("return $T::new", ClassName.get(generatorConfig.getPackageName(), typeName));
            if (index == tableMapList.size() - 1) {
                builder.endControlFlow();
            }
            index++;
        }
        builder.addStatement("return null");
        return builder.build();
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
//                    .addAnnotation(AnnotationSpec.builder(ClassName.get("jakarta.persistence", "Column")).addMember("name", "$S", columnName).addMember("nullable", "$L", isNullable.equals("YES")).build())
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
        String getterName = getFieldGetterMethodName(fieldSpec.name);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(getterName).returns(fieldSpec.type).addModifiers(Modifier.PUBLIC);
        methodBuilder.addStatement("return this." + fieldSpec.name);
        classBuilder.addMethod(methodBuilder.build());
    }

    public String getFieldGetterMethodName(String fieldName) {
        return "get".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName));
    }

    private void addSetter(FieldSpec fieldSpec, TypeSpec.Builder classBuilder, String typeName) {
        String setterName = getFieldSetterMethodName(fieldSpec.name);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setterName).addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(fieldSpec.type, fieldSpec.name);
        methodBuilder.addStatement("this." + fieldSpec.name + " = " + fieldSpec.name);
        if (generatorConfig.getFluentSetter()) {
            methodBuilder.returns(ClassName.get(generatorConfig.getPackageName(), typeName));
            methodBuilder.addStatement("return this");
        }
        classBuilder.addMethod(methodBuilder.build());
    }

    public String getFieldSetterMethodName(String fieldName) {
        return "set".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName));
    }

    public FieldSpec getTableNameField(String tableName) {
        return FieldSpec.builder(String.class, "tableName", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", tableName)
                .build();
    }

    public MethodSpec buildTableNameMethod() {
        return MethodSpec.methodBuilder("getTableName").returns(String.class).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return tableName")
                .build();
    }

    public FieldSpec getKeyNamesField(String tableName) {
        try (ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(generatorConfig.getSchemaName(), null, tableName)) {
            List<String> primaryKeyColumnNameList = new ArrayList<>();
            while (primaryKeys.next()) {
                String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                primaryKeyColumnNameList.add(primaryKeyColumnName);
            }
            return FieldSpec.builder(ArrayTypeName.of(String.class), "keyNames", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                    .initializer("new $T{$L}",
                            ArrayTypeName.of(String.class),
                            CodeBlock.join(primaryKeyColumnNameList.stream().map(name -> CodeBlock.of("$S", name)).collect(Collectors.toList()), ", ")
                    )
                    .build();
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    public MethodSpec buildKeyNamesMethod() {
        return MethodSpec.methodBuilder("getKeyNames").returns(ArrayTypeName.of(String.class)).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return keyNames")
                .build();
    }

    public FieldSpec getColumnNamesField(List<Map<String, Object>> columnMapList) {
        List<String> columnNameList = new ArrayList<>();
        for (Map<String, Object> columnMap : columnMapList) {
            String columnName = (String) columnMap.get("COLUMN_NAME");
            columnNameList.add(columnName);
        }
        return FieldSpec.builder(ArrayTypeName.of(String.class), "columnNames", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("new $T{$L}",
                        ArrayTypeName.of(String.class),
                        CodeBlock.join(columnNameList.stream().map(name -> CodeBlock.of("$S", name)).collect(Collectors.toList()), ", ")
                )
                .build();
    }

    public MethodSpec buildColumnNamesMethod() {
        return MethodSpec.methodBuilder("getColumnNames").returns(ArrayTypeName.of(String.class)).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return columnNames")
                .build();
    }

    public FieldSpec isAutoIncrementField(List<Map<String, Object>> columnMapList) {
        boolean autoIncrement = columnMapList.stream().anyMatch(columnMap -> columnMap.get("IS_AUTOINCREMENT").equals("YES"));
        return FieldSpec.builder(Boolean.class, "autoIncrement", Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("$L", autoIncrement)
                .build();
    }

    public MethodSpec isAutoIncrementMethod() {
        return MethodSpec.methodBuilder("isAutoIncrement").returns(Boolean.class).addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return autoIncrement")
                .build();
    }

    public MethodSpec buildValueMethod(List<Map<String, Object>> columnMapList) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getValue").returns(Object.class).addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(String.class, "columnName").build())
                .addAnnotation(Override.class);

        int index = 0;
        for (Map<String, Object> columnMap : columnMapList) {
            String columnName = (String) columnMap.get("COLUMN_NAME");
            if (index == 0) {
                builder.beginControlFlow("if (columnName.equals($S))", columnName);
            } else {
                builder.nextControlFlow("else if (columnName.equals($S))", columnName);
            }
            builder.addStatement("return $L()", getFieldGetterMethodName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName)));
            if (index == columnMapList.size() - 1) {
                builder.endControlFlow();
            }
            index++;
        }
        builder.addStatement("return null");
        return builder.build();
    }

    private MethodSpec mapToEntityMethod(String typeName, List<Map<String, Object>> columnMapList) {
        ClassName className = ClassName.get(generatorConfig.getPackageName(), typeName);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("mapToEntity")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "result")
                .returns(className);

        builder.addStatement("$T entity = new $T()", className, className);
        for (Map<String, Object> columnMap : columnMapList) {
            String columnName = (String) columnMap.get("COLUMN_NAME");
            int datatype = (int) columnMap.get("DATA_TYPE");
            String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
            builder.addStatement("entity.$L(result.get($S) != null ? ($T) result.get($S) : null)",
                    getFieldSetterMethodName(fieldName),
                    fieldName,
                    getClassName(JDBCType.valueOf(datatype)),
                    fieldName
            );
        }
        builder.addStatement("return entity");
        return builder.build();
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
