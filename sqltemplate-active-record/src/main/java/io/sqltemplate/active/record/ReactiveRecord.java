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
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sqltemplate.active.record.model.update.ValueSet.set;

public class ReactiveRecord<T> extends TableRecord<T> {

    public static <T> Mono<T> get(Object... values) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        return where(record, record.getKeyEQValues(values)).first();
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<E> getOne(String tableName) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) Objects.requireNonNull(recordIndex).getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns).first();
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> getMany(String tableName) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) Objects.requireNonNull(recordIndex).getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record).on(joinColumns).list();
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> getManyByJoin(String tableName) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) Objects.requireNonNull(recordIndex).getRecordSupplier(tableName).get();
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        return where(record).on(joinTable).list();
    }

    public <E> Mono<E> addOne(String tableName, ReactiveRecord<E> entityRecord) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(entityRecord, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> addMany(String tableName, ReactiveRecord<E>... entityRecords) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) Objects.requireNonNull(recordIndex).getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new))
                .then(record.list());
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> addManyByJoin(String tableName, ReactiveRecord<E>... entityRecords) {
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        List<ReactiveRecord<?>> joinRecords = Arrays.stream(entityRecords)
                .map(entityRecord ->
                        (ReactiveRecord<?>) Objects.requireNonNull(recordIndex).getRecordSupplier(joinTable.getName()).get().mapToEntity(
                                Stream.concat(
                                        joinTable.getJoinColumns().stream().map(joinColumn -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, joinColumn.getReferencedColumnName()), getValue(joinColumn.getName()))),
                                        joinTable.getInverseJoinColumns().stream().map(joinColumn -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, joinColumn.getReferencedColumnName()), entityRecord.getValue(joinColumn.getName())))
                                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))

                        )
                )
                .collect(Collectors.toList());
        return insertAll(joinRecords.toArray(new ReactiveRecord[]{})).then(getManyByJoin(tableName));
    }

    public <E> Mono<E> removeOne(String tableName, ReactiveRecord<E> record) {
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return update(record, joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
    }

    @SuppressWarnings("unchecked")
    public <E> Mono<List<E>> removeMany(String tableName, ReactiveRecord<E>... entityRecords) {
        ReactiveRecord<E> record = (ReactiveRecord<E>) Objects.requireNonNull(recordIndex).getRecordSupplier(tableName).get();
        JoinColumns joinColumns = joinColumnsMap.get(getTableName()).get(tableName);
        return where(record, getKeyEQValues(entityRecords))
                .updateAll(joinColumns.getJoinColumns().stream().map(joinColumn -> set(joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new))
                .then(record.list());
    }

    public <E> Mono<Long> removeManyByJoin(String tableName, ReactiveRecord<E>... entityRecords) {
        JoinTable joinTable = joinTableMap.get(getTableName()).get(tableName);
        ReactiveRecord<?> joinRecord = (ReactiveRecord<?>) Objects.requireNonNull(recordIndex).getRecordSupplier(joinTable.getName()).get();
        return where(joinRecord,
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

    public static <T> Mono<List<T>> all() {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        return record.list();
    }

    public Mono<List<T>> list() {
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
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.queryList();
    }

    public static <T> Mono<T> firstOfAll() {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        return record.first();
    }

    public static <T> Mono<T> lastOfAll(String... fileNames) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        return record.last(fileNames);
    }

    public Mono<T> first() {
        limit(1);
        Mono<List<T>> list = list();
        return list.flatMap(item -> Mono.justOrEmpty(item != null ? item.get(0) : null));
    }

    public Mono<T> last(String... fileNames) {
        limit(1);
        if (fileNames == null) {
            orderBy(Arrays.stream(getKeyNames()).map(DESC::desc).collect(Collectors.toList()));
        } else {
            orderBy(Arrays.stream(fileNames).map(DESC::desc).collect(Collectors.toList()));
        }
        Mono<List<T>> list = list();
        return list.flatMap(item -> Mono.justOrEmpty(item != null ? item.get(0) : null));
    }

    public static <T> Mono<Integer> allCount() {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        return record.count();
    }

    public Mono<Integer> count() {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", getConditionals());
        }};
        return new R2DBCAdapter<Integer>("stg/record/select.stg", "selectCount", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
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
            put("values", getValueExpressions());
        }};
        return new R2DBCAdapter<T>("stg/record/insert.stg", "insert", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.update().then(where(this, getInsertKeyEQValues()).first());
    }

    public static <T> Mono<List<T>> insertAll(ReactiveRecord<T>... records) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        return new R2DBCAdapter<T>("stg/record/insert.stg", "insertAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update().then(where(record, record.getInsertKeyEQValues(records)).list());
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

    public static <T> Mono<List<T>> updateAll(ReactiveRecord<T>... records) {
        for (ReactiveRecord<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        ReactiveRecord<T> record = new ReactiveRecord<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        return new R2DBCAdapter<T>("stg/record/update.stg", "updateAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update().then(where(record, record.getKeyEQValues(records)).list());
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

    public static <T> Mono<Long> deleteAll(ReactiveRecord<T>... records) {
        for (ReactiveRecord<T> record : records) {
            where(record, record.getKeyEQValues());
        }
        ReactiveRecord<T> record = new ReactiveRecord<>();
        where(record, record.getKeyEQValues(records));
        return record.deleteAll();
    }

    public static <T> ReactiveRecord<T> where(Conditional conditional) {
        return (ReactiveRecord<T>) TableRecord.where(conditional);
    }

    public static <T> ReactiveRecord<T> where(Conditional... conditionals) {
        return (ReactiveRecord<T>) TableRecord.where(conditionals);
    }

    public static <T> ReactiveRecord<T> where() {
        return (ReactiveRecord<T>) TableRecord.where();
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
    public ReactiveRecord<T> on(JoinColumns joinColumns) {
        return (ReactiveRecord<T>) super.on(joinColumns);
    }

    @Override
    public <J> ReactiveRecord<T> on(JoinTable joinTable) {
        return (ReactiveRecord<T>) super.on(joinTable);
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
