package io.sqltemplate.active.record.model;

import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.sort.DESC.DESC;
import static io.sqltemplate.active.record.model.update.ValueSet.SETS;

public class Record<T> {

    private List<Conditional> conditionals;
    private List<Sort> sorts;
    private Integer limit;
    private Integer offset;

    public List<Conditional> getConditionals() {
        return conditionals;
    }

    public void setConditionals(List<Conditional> conditionals) {
        this.conditionals = conditionals;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    private String getTableName() {
        return null;
    }

    private String getKeyName() {
        return null;
    }

    private Object getKeyValue() {
        return null;
    }

    private List<String> getColumnNames() {
        return null;
    }

    private List<Object> getValues() {
        return null;
    }

    private T mapToEntity(Map<String, Object> result) {
        return null;
    }

    public Record<T> and(Conditional conditional) {
        if (this.conditionals == null) {
            this.conditionals = new ArrayList<>();
        }
        this.conditionals.add(conditional);
        return this;
    }

    public Record<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Record<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public Record<T> orderBy(Sort sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
        return this;
    }

    public Record<T> orderBy(Collection<Sort> sorts) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.addAll(sorts);
        return this;
    }

    public static <T> Record<T> where(Conditional conditional) {
        Record<T> record = new Record<>();
        return where(record, conditional);
    }

    public static <T> Record<T> where(Record<T> record, Conditional conditional) {
        record.setConditionals(new ArrayList<>());
        record.getConditionals().add(conditional);
        return record;
    }

    static public <T> T get(Object value) throws SQLException {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        return record.first();
    }

    public static <T> List<T> all() throws SQLException {
        Record<T> record = new Record<>();
        return record.list();
    }

    public List<T> list() throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("conditionals", conditionals);
            put("sorts", sorts);
            put("limit", limit);
            put("offset", offset);
        }};
        return new JDBCAdapter<T>("stg/record/select.stg", "select", params) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.queryList();
    }

    public static <T> T firstOfAll() throws SQLException {
        Record<T> record = new Record<>();
        return record.first();
    }

    public static <T> T lastOfAll(String... fileNames) throws SQLException {
        Record<T> record = new Record<>();
        return record.last(fileNames);
    }

    public T first() throws SQLException {
        limit(1);
        List<T> list = list();
        return list != null ? list.get(0) : null;
    }

    public T last(String... fileNames) throws SQLException {
        limit(1);
        if (fileNames == null) {
            orderBy(DESC(getKeyName()));
        } else {
            orderBy(Arrays.stream(fileNames).map(DESC::DESC).collect(Collectors.toList()));
        }
        List<T> list = list();
        return list != null ? list.get(0) : null;
    }

    public static <T> int allCount() throws SQLException {
        Record<T> record = new Record<>();
        return record.count();
    }

    public int count() throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", conditionals);
        }};
        return new JDBCAdapter<Integer>("stg/record/select.stg", "selectCount", params) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.query();
    }

    public boolean exists() throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", conditionals);
        }};
        return new JDBCAdapter<Integer>("stg/record/select.stg", "selectExist", params) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.query() > 0;
    }

    public T insert() throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("values", getValues());
        }};
        new JDBCAdapter<T>("stg/record/insert.stg", "insert", params) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }.update();
        where(this, EQ(getKeyName(), LAST_INSERT_ID));
        return first();
    }

    public static <T> List<T> insertAll(Record<T>... records) throws SQLException {
        Record<T> record = new Record<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        new JDBCAdapter<T>("stg/record/insert.stg", "insertAll", params) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update();
        where(record, GTE(record.getKeyName(), LAST_INSERT_ID));
        return record.list();
    }

    public T update() throws SQLException {
        return update(getKeyValue(), SETS(getColumnNames(), getValues()));
    }

    public static <T> T update(Object value, ValueSet... sets) throws SQLException {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        record.updateAll(sets);
        return record.first();
    }

    public boolean updateAll(ValueSet... sets) throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("sets", sets);
            put("conditionals", conditionals);
        }};
        return new JDBCAdapter<Integer>("stg/record/update.stg", "update", params) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update() > 0;
    }

    public static <T> List<T> updateAll(Record<?>... records) throws SQLException {
        Record<T> record = new Record<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        new JDBCAdapter<T>("stg/record/update.stg", "updateAll", params) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }.update();
        where(record, IN(record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList())));
        return record.list();
    }

    public boolean delete() throws SQLException {
        return delete(getKeyValue());
    }

    public static <T> boolean delete(Object value) throws SQLException {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        return record.deleteAll() > 0;
    }

    public long deleteAll() throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", getTableName());
            put("conditionals", conditionals);
        }};
        return new JDBCAdapter<Integer>("stg/record/delete.stg", "delete", params) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update();
    }

    public static <T> long deleteAll(Record<T>... records) throws SQLException {
        Record<T> record = new Record<>();
        where(record, IN(record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList())));
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("table", record.getTableName());
            put("conditionals", record.getConditionals());
        }};
        return new JDBCAdapter<Integer>("stg/record/delete.stg", "delete", params) {
            @Override
            protected Integer map(Map<String, Object> result) {
                return (Integer) result.values().iterator().next();
            }
        }.update();
    }
}
