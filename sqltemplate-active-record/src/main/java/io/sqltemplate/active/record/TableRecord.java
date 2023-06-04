package io.sqltemplate.active.record;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.conditional.AND;
import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.conditional.EQ;
import io.sqltemplate.active.record.model.conditional.GT;
import io.sqltemplate.active.record.model.conditional.GTE;
import io.sqltemplate.active.record.model.conditional.IN;
import io.sqltemplate.active.record.model.conditional.LK;
import io.sqltemplate.active.record.model.conditional.LT;
import io.sqltemplate.active.record.model.conditional.LTE;
import io.sqltemplate.active.record.model.conditional.NEQ;
import io.sqltemplate.active.record.model.conditional.NIL;
import io.sqltemplate.active.record.model.conditional.NIN;
import io.sqltemplate.active.record.model.conditional.NLK;
import io.sqltemplate.active.record.model.conditional.NNIL;
import io.sqltemplate.active.record.model.conditional.OR;
import io.sqltemplate.active.record.model.join.JoinColumns;
import io.sqltemplate.active.record.model.join.JoinTable;
import io.sqltemplate.active.record.model.sort.ASC;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.template.Parameter;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.update.ValueSet.set;

public class TableRecord<T> {

    public static String DEFAULT_ALIAS = "t";

    public static final Map<String, Map<String, JoinColumns>> joinColumnsMap = new ConcurrentHashMap<>();

    public static final Map<String, Map<String, JoinTable>> joinTableMap = new ConcurrentHashMap<>();

    public static final RecordIndex recordIndex = RecordIndex.provider();

    public static void registerJoinColumn(String tableName, String joinTableName, Function<JoinColumns, JoinColumns> JoinColumnsBuilder) {
        joinColumnsMap.computeIfAbsent(tableName, k -> new ConcurrentHashMap<>());
        joinColumnsMap.get(tableName).put(joinTableName, JoinColumnsBuilder.apply(joinColumnsMap.get(tableName).getOrDefault(joinTableName, new JoinColumns())));
    }

    public static void registerJoinTable(String tableName, String joinTableName, Function<JoinTable, JoinTable> joinTableBuilder) {
        joinTableMap.computeIfAbsent(tableName, k -> new ConcurrentHashMap<>());
        joinTableMap.get(tableName).put(joinTableName, joinTableBuilder.apply(joinTableMap.get(tableName).getOrDefault(joinTableName, new JoinTable())));
    }

    public static void registerEntityClass(Class<? extends TableRecord<?>> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            String tableName = entityClass.getAnnotation(Table.class).name();
            List<Method> joinColumnsMethods = Arrays.stream(entityClass.getMethods())
                    .filter(method ->
                            method.isAnnotationPresent(OneToOne.class) ||
                                    method.isAnnotationPresent(OneToMany.class) ||
                                    method.isAnnotationPresent(ManyToOne.class)
                    )
                    .collect(Collectors.toList());
            for (Method method : joinColumnsMethods) {
                String joinTableName = method.getReturnType().getAnnotation(Table.class).name();
                registerJoinColumn(tableName, joinTableName, joinColumns -> {
                    for (JoinColumn joinColumn : method.getAnnotationsByType(JoinColumn.class)) {
                        joinColumns.addJoinColumn(joinColumn);
                    }
                    return joinColumns;
                });
                registerJoinColumn(joinTableName, tableName, joinColumns -> {
                    for (JoinColumn joinColumn : method.getAnnotationsByType(JoinColumn.class)) {
                        joinColumns.addReverseJoinColumn(joinColumn);
                    }
                    return joinColumns;
                });
            }

            List<Field> joinColumnsFields = Arrays.stream(entityClass.getFields())
                    .filter(field ->
                            field.isAnnotationPresent(OneToOne.class) ||
                                    field.isAnnotationPresent(OneToMany.class) ||
                                    field.isAnnotationPresent(ManyToOne.class)
                    )
                    .collect(Collectors.toList());
            for (Field field : joinColumnsFields) {
                String joinTableName = field.getType().getAnnotation(Table.class).name();
                registerJoinColumn(tableName, joinTableName, joinColumns -> {
                    for (JoinColumn joinColumn : field.getAnnotationsByType(JoinColumn.class)) {
                        joinColumns.addJoinColumn(joinColumn);
                    }
                    return joinColumns;
                });
                registerJoinColumn(joinTableName, tableName, joinColumns -> {
                    for (JoinColumn joinColumn : field.getAnnotationsByType(JoinColumn.class)) {
                        joinColumns.addReverseJoinColumn(joinColumn);
                    }
                    return joinColumns;
                });
            }

            List<Method> joinTableMethods = Arrays.stream(entityClass.getMethods())
                    .filter(method -> method.isAnnotationPresent(ManyToMany.class))
                    .collect(Collectors.toList());
            for (Method method : joinTableMethods) {
                String joinTableName = method.getReturnType().getAnnotation(Table.class).name();
                registerJoinTable(tableName, joinTableName, joinTable -> {
                    jakarta.persistence.JoinTable joinTableAnnotation = method.getAnnotation(jakarta.persistence.JoinTable.class);
                    joinTable.setName(joinTableAnnotation.name());
                    for (JoinColumn joinColumn : joinTableAnnotation.joinColumns()) {
                        joinTable.addJoinColumn(joinColumn);
                    }
                    for (JoinColumn joinColumn : joinTableAnnotation.inverseJoinColumns()) {
                        joinTable.addInverseJoinColumn(joinColumn);
                    }
                    return joinTable;
                });
                registerJoinTable(joinTableName, tableName, joinTable -> {
                    jakarta.persistence.JoinTable joinTableAnnotation = method.getAnnotation(jakarta.persistence.JoinTable.class);
                    joinTable.setName(joinTableAnnotation.name());
                    for (JoinColumn joinColumn : joinTableAnnotation.joinColumns()) {
                        joinTable.addReverseJoinColumn(joinColumn);
                    }
                    for (JoinColumn joinColumn : joinTableAnnotation.inverseJoinColumns()) {
                        joinTable.addReverseInverseJoinColumn(joinColumn);
                    }
                    return joinTable;
                });
            }

            List<Field> joinTableFields = Arrays.stream(entityClass.getFields())
                    .filter(field -> field.isAnnotationPresent(ManyToMany.class))
                    .collect(Collectors.toList());
            for (Field field : joinTableFields) {
                String joinTableName = field.getType().getAnnotation(Table.class).name();
                registerJoinTable(tableName, joinTableName, joinTable -> {
                    jakarta.persistence.JoinTable joinTableAnnotation = field.getAnnotation(jakarta.persistence.JoinTable.class);
                    joinTable.setName(joinTableAnnotation.name());
                    for (JoinColumn joinColumn : joinTableAnnotation.joinColumns()) {
                        joinTable.addJoinColumn(joinColumn);
                    }
                    for (JoinColumn joinColumn : joinTableAnnotation.inverseJoinColumns()) {
                        joinTable.addInverseJoinColumn(joinColumn);
                    }
                    return joinTable;
                });
                registerJoinTable(joinTableName, tableName, joinTable -> {
                    jakarta.persistence.JoinTable joinTableAnnotation = field.getAnnotation(jakarta.persistence.JoinTable.class);
                    joinTable.setName(joinTableAnnotation.name());
                    for (JoinColumn joinColumn : joinTableAnnotation.joinColumns()) {
                        joinTable.addReverseJoinColumn(joinColumn);
                    }
                    for (JoinColumn joinColumn : joinTableAnnotation.inverseJoinColumns()) {
                        joinTable.addReverseInverseJoinColumn(joinColumn);
                    }
                    return joinTable;
                });
            }
        }
    }

    private Transactional.TxType txType = Transactional.TxType.REQUIRED;
    private Class<?>[] rollbackOn = {};
    private Class<?>[] dontRollbackOn = {};

    private String alias = DEFAULT_ALIAS;
    private String joinAlias = "j";
    private JoinTable joinTable;
    private List<Conditional> conditionals;
    private List<Sort> sorts;
    private Integer limit;
    private Integer offset;
    private Boolean autoIncrement = false;

    public TableRecord() {
    }

    public Transactional.TxType getTxType() {
        return txType;
    }

    public TableRecord<T> setTxType(Transactional.TxType txType) {
        this.txType = txType;
        return this;
    }

    public Class<?>[] getRollbackOn() {
        return rollbackOn;
    }

    public TableRecord<T> setRollbackOn(Class<?>[] rollbackOn) {
        this.rollbackOn = rollbackOn;
        return this;
    }

    public Class<?>[] getDontRollbackOn() {
        return dontRollbackOn;
    }

    public TableRecord<T> setDontRollbackOn(Class<?>[] dontRollbackOn) {
        this.dontRollbackOn = dontRollbackOn;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public TableRecord<T> setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public TableRecord<T> setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
        return this;
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public TableRecord<T> setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public List<Conditional> getConditionals() {
        return conditionals;
    }

    public TableRecord<T> setConditionals(List<Conditional> conditionals) {
        this.conditionals = conditionals;
        return this;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public TableRecord<T> setSorts(List<Sort> sorts) {
        this.sorts = sorts;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public TableRecord<T> setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public TableRecord<T> setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Boolean isAutoIncrement() {
        return autoIncrement;
    }

    public TableRecord<T> setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }

    public String getTableName() {
        return this.getClass().getAnnotation(Table.class).name();
    }

    public String[] getKeyNames() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(field -> field.getAnnotation(Column.class).name())
                .toArray(String[]::new);
    }

    public String[] getColumnNames() {
        return Arrays.stream(this.getClass().getFields())
                .map(field -> field.getAnnotation(Column.class).name())
                .toArray(String[]::new);
    }

    public Object getValue(String name) {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.getAnnotation(Column.class).name().equals(name))
                .map(field -> {
                    try {
                        Method method = this.getClass().getMethod("get".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field.getName())));
                        return method.invoke(this);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings({"JavaReflectionInvocation", "unchecked"})
    public T mapToEntity(Map<String, Object> result) {
        try {
            TableRecord<?> tableRecord = recordIndex.getRecordSupplier(getTableName()).get();
            for (Field field : Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.isAnnotationPresent(Column.class))
                    .collect(Collectors.toList())) {
                Method method = tableRecord.getClass().getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field.getName()));
                method.invoke(tableRecord, result.get(field.getName()) != null ? method.getParameters()[0].getType().cast(result.get(field.getName())) : null);
            }
            return (T) tableRecord;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public EQ[] getKeyEQValues() {
        return Arrays.stream(getKeyNames()).map(name -> EQ.eq(name, getValue(name))).toArray(EQ[]::new);
    }

    public Conditional getKeyEQValues(TableRecord<?>... records) {
        return OR.or(Arrays.stream(records).map(TableRecord::getKeyEQValues).map(AND::and).toArray(AND[]::new));
    }

    public EQ[] getInsertKeyEQValues() {
        if (isAutoIncrement()) {
            return new EQ[]{EQ.eq(getKeyNames()[0], LAST_INSERT_ID)};
        } else {
            return Arrays.stream(getKeyNames()).map(name -> EQ.eq(name, getValue(name))).toArray(EQ[]::new);
        }
    }

    public Conditional getInsertKeyEQValues(TableRecord<?>... records) {
        if (isAutoIncrement()) {
            return GTE.gte(getKeyNames()[0], LAST_INSERT_ID);
        } else {
            return OR.or(Arrays.stream(records).map(TableRecord::getKeyEQValues).map(AND::and).toArray(AND[]::new));
        }
    }

    public EQ[] getKeyEQValues(Object... values) {
        return IntStream.range(0, getKeyNames().length).mapToObj(index -> EQ.eq(getKeyNames()[index], values[index])).toArray(EQ[]::new);
    }

    public List<Object> getKeyValues() {
        return Arrays.stream(getKeyNames()).map(this::getValue).collect(Collectors.toList());
    }

    public List<Parameter> getValues() {
        return Arrays.stream(getColumnNames()).map(this::getValue).map(Parameter::new).collect(Collectors.toList());
    }

    public List<ValueSet> getValueSets() {
        return Arrays.stream(getColumnNames()).map(name -> set(name, getValue(name))).collect(Collectors.toList());
    }

    public TableRecord<T> and(Conditional conditional) {
        if (this.conditionals == null) {
            this.conditionals = new ArrayList<>();
        }
        this.conditionals.add(conditional);
        return this;
    }

    public TableRecord<T> on(JoinColumns joinColumns) {
        joinColumns.getJoinColumns().forEach(joinColumn -> this.and(EQ.eq(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))));
        return this;
    }

    public <J> TableRecord<T> on(JoinTable joinTable) {
        this.setJoinTable(joinTable);
        joinTable.getInverseJoinColumns().forEach(joinColumn -> this.and(EQ.eq(getJoinAlias(), joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))));
        return this;
    }

    public TableRecord<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public TableRecord<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public TableRecord<T> orderBy(Sort sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
        return this;
    }

    public TableRecord<T> orderBy(Collection<Sort> sorts) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.addAll(sorts);
        return this;
    }

    public static <T> TableRecord<T> where(Conditional conditional) {
        TableRecord<T> record = new TableRecord<>();
        return where(record, conditional);
    }

    public static <T> TableRecord<T> where(Conditional... conditionals) {
        TableRecord<T> record = new TableRecord<>();
        return where(record, conditionals);
    }

    public static <T> TableRecord<T> where() {
        TableRecord<T> record = new TableRecord<>();
        return where(record);
    }

    public static <T> TableRecord<T> where(TableRecord<T> record, Conditional conditional) {
        record.setConditionals(new ArrayList<>());
        record.and(conditional);
        return record;
    }

    public static <T> TableRecord<T> where(TableRecord<T> record, Conditional... conditionals) {
        record.setConditionals(new ArrayList<>());
        record.getConditionals().addAll(Arrays.asList(conditionals));
        return record;
    }

    public static <T> TableRecord<T> where(TableRecord<T> record) {
        record.setConditionals(new ArrayList<>());
        return record;
    }

    public TableRecord<T> eq(String columnName, Object expression) {
        and(EQ.eq(columnName, expression));
        return this;
    }

    public TableRecord<T> neq(String columnName, Object expression) {
        and(NEQ.neq(columnName, expression));
        return this;
    }

    public TableRecord<T> gt(String columnName, Object expression) {
        and(GT.gt(columnName, expression));
        return this;
    }

    public TableRecord<T> gte(String columnName, Object expression) {
        and(GTE.gte(columnName, expression));
        return this;
    }

    public TableRecord<T> lt(String columnName, Object expression) {
        and(LT.lt(columnName, expression));
        return this;
    }

    public TableRecord<T> lte(String columnName, Object expression) {
        and(LTE.lte(columnName, expression));
        return this;
    }

    public TableRecord<T> lk(String columnName, Object expression) {
        and(LK.lk(columnName, expression));
        return this;
    }

    public TableRecord<T> nlk(String columnName, Object expression) {
        and(NLK.nlk(columnName, expression));
        return this;
    }

    public TableRecord<T> nil(String columnName) {
        and(NIL.nil(columnName));
        return this;
    }

    public TableRecord<T> nnil(String columnName) {
        and(NNIL.nnil(columnName));
        return this;
    }

    public TableRecord<T> in(String columnName, Collection<Object> expressions) {
        and(IN.in(columnName, expressions));
        return this;
    }

    public TableRecord<T> nin(String columnName, Collection<Object> expressions) {
        and(NIN.nin(columnName, expressions));
        return this;
    }

    public TableRecord<T> in(String columnName, Object... expressions) {
        and(IN.in(columnName, expressions));
        return this;
    }

    public TableRecord<T> nin(String columnName, Object... expressions) {
        and(NIN.nin(columnName, expressions));
        return this;
    }

    public TableRecord<T> or(Function<TableRecord<T>, TableRecord<T>> orConditionBuilder) {
        TableRecord<T> record = new TableRecord<>();
        TableRecord<T> result = orConditionBuilder.apply(record);
        and(OR.or(result.getConditionals()));
        return this;
    }

    public TableRecord<T> or(Collection<Conditional> conditionals) {
        and(OR.or(conditionals));
        return this;
    }

    public TableRecord<T> or(Conditional... conditionals) {
        and(OR.or(conditionals));
        return this;
    }

    public TableRecord<T> asc(String columnName) {
        orderBy(ASC.asc(columnName));
        return this;
    }

    public TableRecord<T> desc(String columnName) {
        orderBy(DESC.desc(columnName));
        return this;
    }
}
