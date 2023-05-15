package io.sqltemplate.active.record;

import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;

import java.util.*;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.sort.DESC.DESC;

public class Record<T> extends TableRecord<T> {

    static public <T> T get(Object value) {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        return record.first();
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
            put("values", getValues());
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
}
