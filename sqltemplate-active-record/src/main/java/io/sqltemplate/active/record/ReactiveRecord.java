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
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import jakarta.persistence.Table;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sqltemplate.active.record.model.update.ValueSet.set;

public class ReactiveRecord<T> extends TableRecord<T> {

    @SuppressWarnings("unchecked")
    public static <T> Mono<T> get(String tableName, Object... values) {
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
        return where(record, record.getKeyEQValues(values)).first();
    }

    public static <T> Mono<T> get(Class<T> recordClass, Object... values) {
        return get(recordClass.getAnnotation(Table.class).name(), values);
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<E> getOne(String tableName) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns, this).first();
    }

    public <E> Mono<E> getOne(Class<E> entityClass) {
        return getOne(entityClass.getAnnotation(Table.class).name());
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> getMany(String tableName) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns, this).list();
    }

    public <E> Mono<List<E>> getMany(Class<E> entityClass) {
        return getMany(entityClass.getAnnotation(Table.class).name());
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> getManyByJoin(String tableName) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        return where(record).on(joinTable, this).list();
    }

    public <E> Mono<List<E>> getManyByJoin(Class<E> entityClass) {
        return getManyByJoin(entityClass.getAnnotation(Table.class).name());
    }

    public <E> Mono<E> addOne(String tableName, ReactiveRecord<E> entityRecord) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(entityRecord, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
    }

    public <E> Mono<E> addOne(Class<E> entityClass, ReactiveRecord<E> entityRecord) {
        return addOne(entityClass.getAnnotation(Table.class).name(), entityRecord);
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> addMany(String tableName, ReactiveRecord<E>... entityRecords) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new))
                .then(record.list());
    }

    public <E> Mono<List<E>> addMany(Class<E> entityClass, ReactiveRecord<E>... entityRecords) {
        return addMany(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> addManyByJoin(String tableName, ReactiveRecord<E>... entityRecords) {
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        List<ReactiveRecord<?>> joinRecords = Arrays.stream(entityRecords)
                .map(entityRecord ->
                        (ReactiveRecord<?>) recordIndex.getRecordSupplier(joinTable.getName()).get().mapToEntity(
                                Stream.concat(
                                        joinTable.getJoinColumns().stream()
                                                .map(joinColumn -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, joinColumn.getReferencedColumnName()), getValue(joinColumn.getName()))),
                                        joinTable.getInverseJoinColumns().stream()
                                                .map(joinColumn -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, joinColumn.getReferencedColumnName()), entityRecord.getValue(joinColumn.getName())))
                                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))

                        )
                )
                .collect(Collectors.toList());
        return insertAll(tableName, joinRecords.toArray(new ReactiveRecord[]{})).then(getManyByJoin(tableName));
    }

    public <E> Mono<List<E>> addManyByJoin(Class<E> entityClass, ReactiveRecord<E>... entityRecords) {
        return addManyByJoin(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    public <E> Mono<E> removeOne(String tableName, ReactiveRecord<E> record) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(record, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
    }

    public <E> Mono<E> removeOne(Class<E> entityClass, ReactiveRecord<E> entityRecord) {
        return removeOne(entityClass.getAnnotation(Table.class).name(), entityRecord);
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> removeMany(String tableName, ReactiveRecord<E>... entityRecords) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) recordIndex.getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new))
                .then(record.list());
    }

    public <E> Mono<List<E>> removeMany(Class<E> entityClass, ReactiveRecord<E>... entityRecords) {
        return removeMany(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    public <E> Mono<Long> removeManyByJoin(String tableName, ReactiveRecord<E>... entityRecords) {
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        ReactiveRecord<?> joinRecord = (ReactiveRecord<?>) recordIndex.getRecordSupplier(joinTable.getName()).get();
        return where(joinRecord,
                OR.or(Arrays.stream(entityRecords)
                        .flatMap(entityRecord ->
                                Stream.concat(
                                        joinTable.getJoinColumns().stream()
                                                .map(joinColumn -> EQ.eq(joinColumn.getName(), getValue(joinColumn.getReferencedColumnName()))),
                                        joinTable.getInverseJoinColumns().stream()
                                                .map(joinColumn -> EQ.eq(joinColumn.getName(), entityRecord.getValue(joinColumn.getReferencedColumnName())))
                                )
                        ).collect(Collectors.toList())
                )
        ).deleteAll();
    }

    public <E> Mono<Long> removeManyByJoin(Class<E> entityClass, ReactiveRecord<E>... entityRecords) {
        return removeManyByJoin(entityClass.getAnnotation(Table.class).name(), entityRecords);
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<List<T>> all(String tableName) {
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.list();
    }

    @SuppressWarnings("unchecked")
    public static <T> T record(String tableName) {
        return (T) recordIndex.getRecordSupplier(tableName).get();
    }

    public static <T> Mono<List<T>> all(Class<T> recordClass) {
        return all(recordClass.getAnnotation(Table.class).name());
    }

    public <T> Mono<List<T>> list() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("conditionals", getConditionals());
            put("sorts", getSorts());
            put("limit", getLimit());
            put("offset", getOffset());
            put("joinTable", getJoinTable());
        }};
        return new R2DBCAdapter<T>("stg/record/select.stg", "select", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @SuppressWarnings("unchecked")
            @Override
            protected T map(Map<String, Object> result) {
                return (T) mapToEntity(result);
            }
        }.queryList();
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<T> firstOfAll(String tableName) {
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.first();
    }

    public static <T> Mono<T> firstOfAll(Class<T> recordClass) {
        return firstOfAll(recordClass.getAnnotation(Table.class).name());
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<T> lastOfAll(String tableName, String... columnNames) {
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.last(columnNames);
    }

    public static <T> Mono<T> lastOfAll(Class<T> recordClass, String... fileNames) {
        return lastOfAll(recordClass.getAnnotation(Table.class).name(), fileNames);
    }

    public Mono<T> first() {
        limit(1);
        Mono<List<T>> list = list();
        return list.flatMap(item -> Mono.justOrEmpty(item != null ? item.get(0) : null));
    }

    public Mono<T> last(String... columnNames) {
        limit(1);
        if (columnNames == null) {
            orderBy(Arrays.stream(getKeyNames()).map(DESC::desc).collect(Collectors.toList()));
        } else {
            orderBy(Arrays.stream(columnNames).map(DESC::desc).collect(Collectors.toList()));
        }
        Mono<List<T>> list = list();
        return list.flatMap(item -> Mono.justOrEmpty(item != null ? item.get(0) : null));
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<Long> allCount(String tableName) {
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
        return record.count();
    }

    public static <T> Mono<Long> allCount(Class<T> recordClass) {
        return allCount(recordClass.getAnnotation(Table.class).name());
    }

    public Mono<Long> count() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new R2DBCAdapter<Long>("stg/record/select.stg", "selectCount", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Long map(Map<String, Object> result) {
                return (Long) result.values().iterator().next();
            }
        }.query();
    }

    public Mono<Boolean> exists() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new R2DBCAdapter<Boolean>("stg/record/select.stg", "selectExist", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Boolean map(Map<String, Object> result) {
                return result.values().iterator().hasNext();
            }
        }.query();
    }

    public Mono<T> insert() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("values", getValues());
        }};
        return new R2DBCAdapter<T>("stg/record/insert.stg", "insert", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.update().then(where(this, getInsertKeyEQValues()).first());
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<List<T>> insertAll(String tableName, ReactiveRecord<T>... records) {
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
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
        return new R2DBCAdapter<T>("stg/record/insert.stg", "insertAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update().then(where(record, record.getInsertKeyEQValues(records)).list());
    }

    public static <T> Mono<List<T>> insertAll(Class<T> recordClass, ReactiveRecord<T>... records) {
        return insertAll(recordClass.getAnnotation(Table.class).name(), records);
    }

    public Mono<T> update() {
        return update(this, getValueSets().toArray(new ValueSet[]{}));
    }

    public static <T> Mono<T> update(ReactiveRecord<T> record, ValueSet... sets) {
        where(record, record.getKeyEQValues());
        return record.updateAll(sets).then(record.first());
    }

    public Mono<Boolean> updateAll(ValueSet... sets) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("sets", sets);
            put("conditionals", getConditionals());
        }};
        return new R2DBCAdapter<Integer>("stg/record/update.stg", "update", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update().map(count -> (long) count > 0);
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<List<T>> updateAll(String tableName, ReactiveRecord<T>... records) {
        for (ReactiveRecord<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
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
        return new R2DBCAdapter<T>("stg/record/update.stg", "updateAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update().then(where(record, record.getKeyEQValues(records)).list());
    }

    public static <T> Mono<List<T>> updateAll(Class<T> recordClass, ReactiveRecord<T>... records) {
        return updateAll(recordClass.getAnnotation(Table.class).name(), records);
    }

    public Mono<Boolean> delete() {
        return delete(this);
    }

    public static <T> Mono<Boolean> delete(ReactiveRecord<T> record) {
        where(record, record.getKeyEQValues());
        return record.deleteAll().map(count -> count > 0);
    }

    public Mono<Long> deleteAll() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new R2DBCAdapter<Integer>("stg/record/delete.stg", "delete", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update();
    }

    @SuppressWarnings("unchecked")
    public static <T> Mono<Long> deleteAll(String tableName, ReactiveRecord<T>... records) {
        for (ReactiveRecord<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        ReactiveRecord<T> record = (ReactiveRecord<T>) recordIndex.getRecordSupplier(tableName).get();
        where(record, record.getKeyEQValues(records));
        return record.deleteAll();
    }

    public static <T> Mono<Long> deleteAll(Class<T> recordClass, ReactiveRecord<T>... records) {
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

    public static <T> ReactiveRecord<T> where(ReactiveRecord<T> record, Conditional conditional) {
        return (ReactiveRecord<T>) TableRecord.where(record, conditional);
    }

    public static <T> ReactiveRecord<T> where(ReactiveRecord<T> record, Conditional... conditionals) {
        return (ReactiveRecord<T>) TableRecord.where(record, conditionals);
    }

    public static <T> ReactiveRecord<T> where(ReactiveRecord<T> record) {
        return (ReactiveRecord<T>) TableRecord.where(record);
    }

    @Override
    public ReactiveRecord<T> and(Conditional conditional) {
        return (ReactiveRecord<T>) super.and(conditional);
    }

    @Override
    public ReactiveRecord<T> on(JoinColumns joinColumns, TableRecord<?> record) {
        return (ReactiveRecord<T>) super.on(joinColumns, record);
    }

    @Override
    public ReactiveRecord<T> on(JoinTable joinTable, TableRecord<?> record) {
        return (ReactiveRecord<T>) super.on(joinTable, record);
    }

    @Override
    public ReactiveRecord<T> limit(int limit) {
        return (ReactiveRecord<T>) super.limit(limit);
    }

    @Override
    public ReactiveRecord<T> offset(int offset) {
        return (ReactiveRecord<T>) super.offset(offset);
    }

    @Override
    public ReactiveRecord<T> orderBy(Sort sort) {
        return (ReactiveRecord<T>) super.orderBy(sort);
    }

    @Override
    public ReactiveRecord<T> orderBy(Collection<Sort> sorts) {
        return (ReactiveRecord<T>) super.orderBy(sorts);
    }

    @Override
    public ReactiveRecord<T> eq(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.eq(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> neq(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.neq(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> gt(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.gt(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> gte(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.gte(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> lt(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.lt(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> lte(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.lte(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> lk(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.lk(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> nlk(String columnName, Object expression) {
        return (ReactiveRecord<T>) super.nlk(columnName, expression);
    }

    @Override
    public ReactiveRecord<T> nil(String columnName) {
        return (ReactiveRecord<T>) super.nil(columnName);
    }

    @Override
    public ReactiveRecord<T> nnil(String columnName) {
        return (ReactiveRecord<T>) super.nnil(columnName);
    }

    @Override
    public ReactiveRecord<T> in(String columnName, Collection<Object> expressions) {
        return (ReactiveRecord<T>) super.in(columnName, expressions);
    }

    @Override
    public ReactiveRecord<T> nin(String columnName, Collection<Object> expressions) {
        return (ReactiveRecord<T>) super.nin(columnName, expressions);
    }

    @Override
    public ReactiveRecord<T> in(String columnName, Object... expressions) {
        return (ReactiveRecord<T>) super.in(columnName, expressions);
    }

    @Override
    public ReactiveRecord<T> nin(String columnName, Object... expressions) {
        return (ReactiveRecord<T>) super.nin(columnName, expressions);
    }

    @Override
    public ReactiveRecord<T> or(Function<TableRecord<T>, TableRecord<T>> orConditionBuilder) {
        return (ReactiveRecord<T>) super.or(orConditionBuilder);
    }

    @Override
    public ReactiveRecord<T> or(Collection<Conditional> conditionals) {
        return (ReactiveRecord<T>) super.or(conditionals);
    }

    @Override
    public ReactiveRecord<T> or(Conditional... conditionals) {
        return (ReactiveRecord<T>) super.or(conditionals);
    }

    @Override
    public ReactiveRecord<T> asc(String columnName) {
        return (ReactiveRecord<T>) super.asc(columnName);
    }

    @Override
    public ReactiveRecord<T> desc(String columnName) {
        return (ReactiveRecord<T>) super.desc(columnName);
    }
}
