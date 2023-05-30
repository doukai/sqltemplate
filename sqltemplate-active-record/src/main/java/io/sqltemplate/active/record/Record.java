package io.sqltemplate.active.record;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.conditional.EQ;
import io.sqltemplate.active.record.model.conditional.OR;
import io.sqltemplate.core.expression.NullValue;
import io.sqltemplate.active.record.model.join.JoinColumns;
import io.sqltemplate.active.record.model.join.JoinTable;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sqltemplate.active.record.model.update.ValueSet.set;

public class Record<T> extends TableRecord<T> {

    public static <T> T get(Object... values) {
        Record<T> record = new Record<>();
        return where(record, record.getKeyEQValues(values)).first();
    }

    @SuppressWarnings("unchecked")
    public <E> E getOne(String tableName) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns).first();
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getMany(String tableName) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns).list();
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getManyByJoin(String tableName) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        return where(record).on(joinTable).list();
    }

    public <E> E addOne(String tableName, Record<E> entityRecord) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(entityRecord, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> addMany(String tableName, Record<E>... entityRecords) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
        return record.list();
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
        insertAll(joinRecords.toArray(new Record[]{}));
        return getManyByJoin(tableName);
    }

    public <E> E removeOne(String tableName, Record<E> entityRecord) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(entityRecord, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> removeMany(String tableName, Record<E>... entityRecords) {
        Record<E> record = (Record<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
        return record.list();
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

    public static <T> List<T> all() {
        Record<T> record = new Record<>();
        return record.list();
    }

    public List<T> list() {
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
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.queryList();
    }

    public static <T> T firstOfAll() {
        Record<T> record = new Record<>();
        return record.first();
    }

    public static <T> T lastOfAll(String... fileNames) {
        Record<T> record = new Record<>();
        return record.last(fileNames);
    }

    public T first() {
        limit(1);
        List<T> list = list();
        return list != null ? list.get(0) : null;
    }

    public T last(String... fileNames) {
        limit(1);
        if (fileNames == null) {
            orderBy(Arrays.stream(getKeyNames()).map(DESC::desc).collect(Collectors.toList()));
        } else {
            orderBy(Arrays.stream(fileNames).map(DESC::desc).collect(Collectors.toList()));
        }
        List<T> list = list();
        return list != null ? list.get(0) : null;
    }

    public static <T> int allCount() {
        Record<T> record = new Record<>();
        return record.count();
    }

    public int count() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new JDBCAdapter<Integer>("stg/record/select.stg", "selectCount", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
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
            put("values", getValueExpressions());
        }};
        new JDBCAdapter<T>("stg/record/insert.stg", "insert", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }
                .update();
        return where(this, getInsertKeyEQValues()).first();
    }

    public static <T> List<T> insertAll(Record<T>... records) {
        Record<T> record = new Record<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        new JDBCAdapter<T>("stg/record/insert.stg", "insertAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }
                .update();
        return where(record, record.getInsertKeyEQValues(records)).list();
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

    public static <T> List<T> updateAll(Record<T>... records) {
        for (Record<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        Record<T> record = new Record<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        new JDBCAdapter<T>("stg/record/update.stg", "updateAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }
                .update();
        return where(record, record.getKeyEQValues(records)).list();
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

    public static <T> long deleteAll(Record<T>... records) {
        for (Record<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        Record<T> record = new Record<>();
        return where(record, record.getKeyEQValues(records)).deleteAll();
    }

    public static <T> Record<T> where(Conditional conditional) {
        return (Record<T>) TableRecord.where(conditional);
    }

    public static <T> Record<T> where(Conditional... conditionals) {
        return (Record<T>) TableRecord.where(conditionals);
    }

    public static <T> Record<T> where() {
        return (Record<T>) TableRecord.where();
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
    public Record<T> on(JoinColumns joinColumns) {
        return (Record<T>) super.on(joinColumns);
    }

    @Override
    public <J> Record<T> on(JoinTable joinTable) {
        return (Record<T>) super.on(joinTable);
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
