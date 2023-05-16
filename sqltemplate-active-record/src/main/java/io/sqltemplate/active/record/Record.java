package io.sqltemplate.active.record;

import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.join.JoinColumn;
import io.sqltemplate.active.record.model.join.JoinTable;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.sort.DESC.DESC;
import static io.sqltemplate.active.record.model.update.ValueSet.SET;

public class Record<T> extends TableRecord<T> {

    public static <T> T get(Object value) {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        return record.first();
    }

    public <E, R extends Record<E>> E getOne(List<JoinColumn> joinColumns, R entityRecord) {
        return Record.where(entityRecord).on(joinColumns).first();
    }

    public <E, R extends Record<E>> List<E> getMany(List<JoinColumn> joinColumns, R entityRecord) {
        return Record.where(entityRecord).on(joinColumns).list();
    }

    public <E, R extends Record<E>> List<E> getMany(JoinTable joinTable, R entityRecord) {
        return Record.where(entityRecord).on(joinTable).list();
    }

    public <E, R extends Record<E>> E addOne(List<JoinColumn> joinColumns, R entityRecord) {
        where(entityRecord, EQ(entityRecord.getKeyName(), entityRecord.getKeyValue()));
        entityRecord.updateAll(joinColumns.stream().map(joinColumn -> SET(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
        return entityRecord.first();
    }

    public <E, R extends Record<E>> List<E> addMany(List<JoinColumn> joinColumns, R... entityRecords) {
        Record<E> entityRecord = new Record<>();
        where(entityRecord, IN(entityRecord.getKeyName(), Arrays.stream(entityRecords).map(TableRecord::getKeyValue).collect(Collectors.toList())));
        entityRecord.updateAll(joinColumns.stream().map(joinColumn -> SET(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))).toArray(ValueSet[]::new));
        return entityRecord.list();
    }

    public <E, R extends Record<E>> List<E> addMany(JoinTable joinTable, R... entityRecords) {
        List<HashMap<String, Object>> joinRecords = Arrays.stream(entityRecords)
                .map(entityRecord ->
                        new HashMap<String, Object>() {{
                            put("tableName", joinTable.getName());
                            put("columnNames", Stream.concat(joinTable.getJoinColumns().stream(), joinTable.getInverseJoinColumns().stream()).map(JoinColumn::getReferencedColumnName).collect(Collectors.toList()));
                            put("values",
                                    Stream.concat(joinTable.getJoinColumns().stream()
                                                            .map(joinColumn -> getValue(joinColumn.getName())),
                                                    joinTable.getInverseJoinColumns().stream()
                                                            .map(joinColumn -> entityRecord.getValue(joinColumn.getName()))
                                            )
                                            .collect(Collectors.toList())
                            );
                        }}

                )
                .collect(Collectors.toList());
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", joinRecords);
        }};
        Record<E> record = new Record<>();
        new JDBCAdapter<Long>("stg/record/insert.stg", "insertAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()).update();
        return getMany(joinTable, record);
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
            orderBy(DESC(getKeyName()));
        } else {
            orderBy(Arrays.stream(fileNames).map(DESC::DESC).collect(Collectors.toList()));
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
        where(this, EQ(getKeyName(), LAST_INSERT_ID));
        return first();
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
        where(record, GTE(record.getKeyName(), LAST_INSERT_ID));
        return record.list();
    }

    public T update() {
        return update(getKeyValue(), getValueSets().toArray(new ValueSet[]{}));
    }

    public static <T> T update(Object value, ValueSet... sets) {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        record.updateAll(sets);
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
            where(record, EQ(record.getKeyName(), record.getKeyValue()));
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
        where(record, IN(record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList())));
        return record.list();
    }

    public boolean delete() {
        return delete(getKeyValue());
    }

    public static <T> boolean delete(Object value) {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        return record.deleteAll() > 0;
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
        Record<T> record = new Record<>();
        where(record, IN(record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList())));
        return record.deleteAll();
    }

    public static <T> Record<T> where(Conditional conditional) {
        Record<T> record = new Record<>();
        return where(record, conditional);
    }

    public static <T> Record<T> where() {
        Record<T> record = new Record<>();
        return where(record);
    }

    public static <T> Record<T> where(Record<T> record, Conditional conditional) {
        return (Record<T>) TableRecord.where(record, conditional);
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
    public Record<T> on(JoinTable joinTable) {
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
    public Record<T> or(Collection<Conditional> conditionals) {
        return (Record<T>) super.or(conditionals);
    }

    @Override
    public Record<T> or(Conditional... conditionals) {
        return (Record<T>) super.or(conditionals);
    }
}
