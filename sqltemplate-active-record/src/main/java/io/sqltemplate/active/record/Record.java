package io.sqltemplate.active.record;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.conditional.EQ;
import io.sqltemplate.active.record.model.conditional.OR;
import io.sqltemplate.active.record.model.expression.NullValue;
import io.sqltemplate.active.record.model.join.JoinColumns;
import io.sqltemplate.active.record.model.join.JoinTable;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import jakarta.persistence.Table;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sqltemplate.active.record.model.update.ValueSet.set;

public class Record<T> extends TableRecord<T> {

    @SuppressWarnings("unchecked")
    public static <T> T get(String tableName, Object... values) {
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        return where(record, record.getKeyEQValues(values)).first();
    }

    public static <T> T get(Class<T> recordClass, Object... values) {
        return get(recordClass.getAnnotation(Table.class).name(), values);
    }

    @SuppressWarnings("unchecked")
    public <E> E getOne(String tableName) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns, this).first();
    }

    public <E> E getOne(Class<E> entityClass) {
        return getOne(entityClass.getAnnotation(Table.class).name());
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getMany(String tableName) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns, this).list();
    }

    public <E> List<E> getMany(Class<E> entityClass) {
        return getMany(entityClass.getAnnotation(Table.class).name());
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getManyByJoin(String tableName) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        return where(record).on(joinTable, this).list();
    }

    public <E> List<E> getManyByJoin(Class<E> entityClass) {
        return getManyByJoin(entityClass.getAnnotation(Table.class).name());
    }

    public <E> E addOne(String tableName, Record<E> entityRecord) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(entityRecord, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
    }

    public <E> E addOne(Class<E> entityClass, Record<E> entityRecord) {
        return addOne(entityClass.getAnnotation(Table.class).name(), entityRecord);
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> addMany(String tableName, Record<E>... entityRecords) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
        return record.list();
    }

    public <E> List<E> addMany(Class<E> entityClass, Record<E>... entityRecords) {
        return addMany(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> addManyByJoin(String tableName, Record<E>... entityRecords) {
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        List<Record<?>> joinRecords = Arrays.stream(entityRecords)
                .map(entityRecord ->
                        (Record<?>) recordIndex.getRecordSupplier(joinTable.getName()).get().mapToEntity(
                                Stream.concat(
                                        joinTable.getJoinColumns().stream()
                                                .map(joinColumn -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, joinColumn.getReferencedColumnName()), getValue(joinColumn.getName()))),
                                        joinTable.getInverseJoinColumns().stream()
                                                .map(joinColumn -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, joinColumn.getReferencedColumnName()), entityRecord.getValue(joinColumn.getName())))
                                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                        )
                )
                .collect(Collectors.toList());
        insertAll(tableName, joinRecords.toArray(new Record[]{}));
        return getManyByJoin(tableName);
    }

    public <E> List<E> addManyByJoin(Class<E> entityClass, Record<E>... entityRecords) {
        return addManyByJoin(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    public <E> E removeOne(String tableName, Record<E> entityRecord) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(entityRecord, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
    }

    public <E> E removeOne(Class<E> entityClass, Record<E> entityRecord) {
        return removeOne(entityClass.getAnnotation(Table.class).name(), entityRecord);
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> removeMany(String tableName, Record<E>... entityRecords) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
        return record.list();
    }

    public <E> List<E> removeMany(Class<E> entityClass, Record<E>... entityRecords) {
        return removeMany(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    @SuppressWarnings("unchecked")
    public <E> long removeManyByJoin(String tableName, Record<E>... entityRecords) {
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        Record<?> joinRecord = (Record<?>) recordIndex.getRecordSupplier(joinTable.getName()).get();
        return where(
                joinRecord,
                OR.or(Arrays.stream(entityRecords)
                        .flatMap(entityRecord ->
                                Stream.concat(
                                        joinTable.getJoinColumns().stream().map(joinColumn -> EQ.eq(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))),
                                        joinTable.getInverseJoinColumns().stream().map(joinColumn -> EQ.eq(joinColumn.getReferencedColumnName(), entityRecord.getValue(joinColumn.getName())))
                                )
                        ).collect(Collectors.toList())
                )
        ).deleteAll();
    }

    public <E> long removeManyByJoin(Class<E> entityClass, Record<E>... entityRecords) {
        return removeManyByJoin(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> all(String tableName) {
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.list();
    }

    @SuppressWarnings("unchecked")
    public static <T> T record(String tableName) {
        return (T) recordIndex.getRecordSupplier(tableName).get();
    }

    public static <T> List<T> all(Class<T> recordClass) {
        return all(recordClass.getAnnotation(Table.class).name());
    }

    public <T> List<T> list() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("conditionals", getConditionals());
            put("sorts", getSorts());
            put("limit", getLimit());
            put("offset", getOffset());
            put("joinTable", getJoinTable());
        }};
        return new JDBCAdapter<T>("stg/record/select.stg", "select", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @SuppressWarnings("unchecked")
            @Override
            protected T map(Map<String, Object> result) {
                return (T) mapToEntity(result);
            }
        }.queryList();
    }

    @SuppressWarnings("unchecked")
    public static <T> T firstOfAll(String tableName) {
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.first();
    }

    public static <T> T firstOfAll(Class<T> recordClass) {
        return firstOfAll(recordClass.getAnnotation(Table.class).name());
    }

    @SuppressWarnings("unchecked")
    public static <T> T lastOfAll(String tableName, String... columnNames) {
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.last(columnNames);
    }

    public static <T> T lastOfAll(Class<T> recordClass, String... fileNames) {
        return lastOfAll(recordClass.getAnnotation(Table.class).name(), fileNames);
    }

    public T first() {
        limit(1);
        List<T> list = list();
        return list != null ? list.get(0) : null;
    }

    public T last(String... columnNames) {
        limit(1);
        if (columnNames == null) {
            orderBy(Arrays.stream(getKeyNames()).map(DESC::desc).collect(Collectors.toList()));
        } else {
            orderBy(Arrays.stream(columnNames).map(DESC::desc).collect(Collectors.toList()));
        }
        List<T> list = list();
        return list != null ? list.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public static <T> long allCount(String tableName) {
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.count();
    }

    public static <T> long allCount(Class<T> recordClass) {
        return allCount(recordClass.getAnnotation(Table.class).name());
    }

    public long count() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new JDBCAdapter<Long>("stg/record/select.stg", "selectCount", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Long map(Map<String, Object> result) {
                return (Long) result.values().iterator().next();
            }
        }.query();
    }

    public boolean exists() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new JDBCAdapter<Boolean>("stg/record/select.stg", "selectExist", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Boolean map(Map<String, Object> result) {
                return result.values().iterator().hasNext();
            }
        }.query();
    }

    public T insert() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("values", getValues());
        }};
        new JDBCAdapter<T>("stg/record/insert.stg", "insert", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.update();
        return where(this, getInsertKeyEQValues()).first();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> insertAll(String tableName, Record<T>... records) {
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", Arrays.stream(records)
                    .map(record -> new HashMap<String, Object>() {{
                        put("table", record.getTableName());
                        put("columns", record.getColumnNames());
                        put("values", record.getValues());
                    }})
                    .collect(Collectors.toList())
            );
        }};
        new JDBCAdapter<T>("stg/record/insert.stg", "insertAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update();
        return where(record, record.getInsertKeyEQValues(records)).list();
    }

    public static <T> List<T> insertAll(Class<T> recordClass, Record<T>... records) {
        return insertAll(recordClass.getAnnotation(Table.class).name(), records);
    }

    public T update() {
        return update(this, getValueSets().toArray(new ValueSet[]{}));
    }

    public static <T> T update(Record<T> record, ValueSet... sets) {
        where(record, record.getKeyEQValues()).updateAll(sets);
        return record.first();
    }

    public boolean updateAll(ValueSet... sets) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("sets", sets);
            put("conditionals", getConditionals());
        }};
        return new JDBCAdapter<Integer>("stg/record/update.stg", "update", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update() > 0;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> updateAll(String tableName, Record<T>... records) {
        for (Record<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", Arrays.stream(records)
                    .map(record -> new HashMap<String, Object>() {{
                        put("table", record.getTableName());
                        put("sets", record.getValueSets());
                        put("conditionals", record.getConditionals());
                    }})
                    .collect(Collectors.toList())
            );
        }};
        new JDBCAdapter<T>("stg/record/update.stg", "updateAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update();
        return where(record, record.getKeyEQValues(records)).list();
    }

    public static <T> List<T> updateAll(Class<T> recordClass, Record<T>... records) {
        return updateAll(recordClass.getAnnotation(Table.class).name(), records);
    }

    public boolean delete() {
        return delete(this);
    }

    public static <T> boolean delete(Record<T> record) {
        return where(record, record.getKeyEQValues()).deleteAll() > 0;
    }

    public long deleteAll() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new JDBCAdapter<Integer>("stg/record/delete.stg", "delete", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update();
    }

    @SuppressWarnings("unchecked")
    public static <T> long deleteAll(String tableName, Record<T>... records) {
        for (Record<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        Record<T> record = (Record<T>) recordIndex.getRecordSupplier(tableName).get();
        return where(record, record.getKeyEQValues(records)).deleteAll();
    }

    public static <T> long deleteAll(Class<T> recordClass, Record<T>... records) {
        return deleteAll(recordClass.getAnnotation(Table.class).name(), records);
    }

    @SuppressWarnings("unchecked")
    public static <T> T where(String tableName, Conditional conditional) {
        return (T) TableRecord.where(recordIndex.getRecordSupplier(tableName).get(), conditional);
    }

    public static <T> T where(Class<T> recordClass, Conditional conditional) {
        return where(recordClass.getAnnotation(Table.class).name(), conditional);
    }

    @SuppressWarnings("unchecked")
    public static <T> T where(String tableName, Conditional... conditionals) {
        return (T) TableRecord.where(recordIndex.getRecordSupplier(tableName).get(), conditionals);
    }

    public static <T> T where(Class<T> recordClass, Conditional... conditionals) {
        return where(recordClass.getAnnotation(Table.class).name(), conditionals);
    }

    @SuppressWarnings("unchecked")
    public static <T> T where(String tableName) {
        return (T) TableRecord.where(recordIndex.getRecordSupplier(tableName).get());
    }

    public static <T> T where(Class<T> recordClass) {
        return where(recordClass.getAnnotation(Table.class).name());
    }

    public static <T> Record<T> where(Record<T> record, Conditional conditional) {
        return (Record<T>) TableRecord.where(record, conditional);
    }

    public static <T> Record<T> where(Record<T> record, Conditional... conditionals) {
        return (Record<T>) TableRecord.where(record, conditionals);
    }

    public static <T> Record<T> where(Record<T> record) {
        return (Record<T>) TableRecord.where(record);
    }

    @Override
    public Record<T> and(Conditional conditional) {
        return (Record<T>) super.and(conditional);
    }

    @Override
    public Record<T> on(JoinColumns joinColumns, TableRecord<?> record) {
        return (Record<T>) super.on(joinColumns, record);
    }

    @Override
    public Record<T> on(JoinTable joinTable, TableRecord<?> record) {
        return (Record<T>) super.on(joinTable, record);
    }

    @Override
    public Record<T> limit(int limit) {
        return (Record<T>) super.limit(limit);
    }

    @Override
    public Record<T> offset(int offset) {
        return (Record<T>) super.offset(offset);
    }

    @Override
    public Record<T> orderBy(Sort sort) {
        return (Record<T>) super.orderBy(sort);
    }

    @Override
    public Record<T> orderBy(Collection<Sort> sorts) {
        return (Record<T>) super.orderBy(sorts);
    }

    @Override
    public Record<T> eq(String columnName, Object expression) {
        return (Record<T>) super.eq(columnName, expression);
    }

    @Override
    public Record<T> neq(String columnName, Object expression) {
        return (Record<T>) super.neq(columnName, expression);
    }

    @Override
    public Record<T> gt(String columnName, Object expression) {
        return (Record<T>) super.gt(columnName, expression);
    }

    @Override
    public Record<T> gte(String columnName, Object expression) {
        return (Record<T>) super.gte(columnName, expression);
    }

    @Override
    public Record<T> lt(String columnName, Object expression) {
        return (Record<T>) super.lt(columnName, expression);
    }

    @Override
    public Record<T> lte(String columnName, Object expression) {
        return (Record<T>) super.lte(columnName, expression);
    }

    @Override
    public Record<T> lk(String columnName, Object expression) {
        return (Record<T>) super.lk(columnName, expression);
    }

    @Override
    public Record<T> nlk(String columnName, Object expression) {
        return (Record<T>) super.nlk(columnName, expression);
    }

    @Override
    public Record<T> nil(String columnName) {
        return (Record<T>) super.nil(columnName);
    }

    @Override
    public Record<T> nnil(String columnName) {
        return (Record<T>) super.nnil(columnName);
    }

    @Override
    public Record<T> in(String columnName, Collection<Object> expressions) {
        return (Record<T>) super.in(columnName, expressions);
    }

    @Override
    public Record<T> nin(String columnName, Collection<Object> expressions) {
        return (Record<T>) super.nin(columnName, expressions);
    }

    @Override
    public Record<T> in(String columnName, Object... expressions) {
        return (Record<T>) super.in(columnName, expressions);
    }

    @Override
    public Record<T> nin(String columnName, Object... expressions) {
        return (Record<T>) super.nin(columnName, expressions);
    }

    @Override
    public Record<T> or(Function<TableRecord<T>, TableRecord<T>> orConditionBuilder) {
        return (Record<T>) super.or(orConditionBuilder);
    }

    @Override
    public Record<T> or(Collection<Conditional> conditionals) {
        return (Record<T>) super.or(conditionals);
    }

    @Override
    public Record<T> or(Conditional... conditionals) {
        return (Record<T>) super.or(conditionals);
    }

    @Override
    public Record<T> asc(String columnName) {
        return (Record<T>) super.asc(columnName);
    }

    @Override
    public Record<T> desc(String columnName) {
        return (Record<T>) super.desc(columnName);
    }
}
