package io.sqltemplate.active.record;

import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.conditional.EQ;
import io.sqltemplate.active.record.model.expression.NullValue;
import io.sqltemplate.active.record.model.join.JoinColumn;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.conditional.OR.OR;
import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.sort.DESC.DESC;
import static io.sqltemplate.active.record.model.update.ValueSet.SET;

public class Record<T> extends TableRecord<T> {

    public static <T> T get(Object... values) {
        Record<T> record = new Record<>();
        where(record, IntStream.range(0, record.getKeyNames().size()).mapToObj(index -> EQ(record.getAlias(), record.getKeyNames().get(index), values[index])).toArray(EQ[]::new));
        return record.first();
    }

    public <E> E getOne(Record<E> record) {
        return where(record).on(record.getJoinColumns(getTableName())).first();
    }

    public <E> List<E> getMany(Supplier<Record<E>> entityRecordSupplier) {
        Record<E> record = entityRecordSupplier.get();
        return where(record).on(record.getJoinColumns(getTableName())).list();
    }

    public <E, J> List<E> getJoins(Supplier<Record<E>> entityRecordSupplier, Supplier<Record<J>> joinRecordSupplier) {
        Record<E> record = entityRecordSupplier.get();
        Record<J> joinRecord = joinRecordSupplier.get();
        return where(record).on(joinRecord.getJoinColumns()).list();
    }

    public <E> E addOne(Record<E> record) {
        return update(record, record.getJoinColumns(getTableName()).stream().map(joinColumn -> SET(record.getAlias(), joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
    }

    public <E> List<E> addMany(Supplier<Record<E>> entityRecordSupplier, Record<E>... entityRecords) {
        Record<E> entityRecord = entityRecordSupplier.get();
        where(entityRecord, IN(getAlias(), entityRecord.getKeyName(), Arrays.stream(entityRecords).map(TableRecord::getKeyValue).collect(Collectors.toList())))
                .updateAll(entityRecord.getJoinColumns(getTableName()).stream().map(joinColumn -> SET(getAlias(), joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
        return entityRecord.list();
    }

    @SuppressWarnings("unchecked")
    public <E, J> List<E> addJoins(Supplier<Record<E>> entityRecordSupplier, Supplier<Record<J>> joinRecordSupplier, Record<E>... entityRecords) {
        List<Record<J>> joinRecords = Arrays.stream(entityRecords)
                .map(entityRecord -> {
                            Record<J> joinRecord = joinRecordSupplier.get();
                            return (Record<J>) joinRecord.mapToEntity(
                                    Stream.concat(
                                                    joinRecord.getJoinColumns().stream().map(joinColumn -> new AbstractMap.SimpleEntry<>(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))),
                                                    joinRecord.getInverseJoinColumns().stream().map(joinColumn -> new AbstractMap.SimpleEntry<>(joinColumn.getReferencedColumnName(), entityRecord.getValue(joinColumn.getName())))
                                            )
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))

                            );
                        }
                )
                .collect(Collectors.toList());
        insertAll(joinRecords.toArray(new Record[]{}));
        return getJoins(entityRecordSupplier, joinRecordSupplier);
    }

    public <E> E removeOne(Record<E> record) {
        return update(record, record.getJoinColumns(getTableName()).stream().map(joinColumn -> SET(record.getAlias(), joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
    }

    public <E> List<E> removeMany(Supplier<Record<E>> entityRecordSupplier, Record<E>... entityRecords) {
        Record<E> entityRecord = entityRecordSupplier.get();
        where(entityRecord, IN(getAlias(), entityRecord.getKeyName(), Arrays.stream(entityRecords).map(TableRecord::getKeyValue).collect(Collectors.toList())))
                .updateAll(entityRecord.getJoinColumns(getTableName()).stream().map(joinColumn -> SET(getAlias(), joinColumn.getReferencedColumnName(), new NullValue())).toArray(ValueSet[]::new));
        return entityRecord.list();
    }

    public <E, J> long removeJoins(Supplier<Record<J>> joinRecordSupplier, Record<E>... entityRecords) {
        Record<J> joinRecord = joinRecordSupplier.get();
        return where(joinRecord,
                OR(Arrays.stream(entityRecords)
                        .flatMap(entityRecord ->
                                Stream.concat(
                                        joinRecord.getJoinColumns().stream().map(joinColumn -> EQ(getAlias(), joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))),
                                        joinRecord.getInverseJoinColumns().stream().map(joinColumn -> EQ(getAlias(), joinColumn.getReferencedColumnName(), entityRecord.getValue(joinColumn.getName())))
                                )
                        ).collect(Collectors.toList())
                )
        )
                .deleteAll();
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
            put("joinRecord", getJoinRecord());
        }};
        return new JDBCAdapter<T>("stg/record/select.stg", "select", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }
                .queryList();
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
            orderBy(DESC(getAlias(), getKeyName()));
        } else {
            orderBy(Arrays.stream(fileNames).map(fileName -> DESC(getAlias(), fileName)).collect(Collectors.toList()));
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
        }
                .query();
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
        }
                .query();
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
        return where(this, EQ(getAlias(), getKeyName(), LAST_INSERT_ID)).first();
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
        return where(record, GTE(record.getAlias(), record.getKeyName(), LAST_INSERT_ID)).list();
    }

    public T update() {
        return update(this, getValueSets().toArray(new ValueSet[]{}));
    }

    public static <T> T update(Record<T> record, ValueSet... sets) {
        where(record, EQ(record.getAlias(), record.getKeyName(), record.getKeyValue())).updateAll(sets);
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
        }
                .update() > 0;
    }

    public static <T> List<T> updateAll(Record<T>... records) {
        for (Record<T> record : records) {
            where(record, EQ(record.getAlias(), record.getKeyName(), record.getKeyValue()));
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
        return where(record, IN(record.getAlias(), record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList()))).list();
    }

    public boolean delete() {
        return delete(this);
    }

    public static <T> boolean delete(Record<T> record) {
        return where(record, EQ(record.getAlias(), record.getKeyName(), record.getKeyValue())).deleteAll() > 0;
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
        }
                .update();
    }

    public static <T> long deleteAll(Record<T>... records) {
        for (Record<T> record : records) {
            where(record, EQ(record.getAlias(), record.getKeyName(), record.getKeyValue()));
        }
        Record<T> record = new Record<>();
        return where(record, IN(record.getAlias(), record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList()))).deleteAll();
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
    public Record<T> on(List<JoinColumn> joinColumns) {
        return (Record<T>) super.on(joinColumns);
    }

    @Override
    public <J> Record<T> on(Record<J> joinRecord) {
        return (Record<T>) super.on(joinRecord);
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
}
