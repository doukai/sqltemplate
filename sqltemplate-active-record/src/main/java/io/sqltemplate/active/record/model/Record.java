package io.sqltemplate.active.record.model;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.annotation.Column;
import io.sqltemplate.active.record.annotation.Table;
import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.expression.Value;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.jdbc.JDBCAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.sort.DESC.DESC;
import static io.sqltemplate.active.record.model.update.ValueSet.SET;

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

    protected String getTableName() {
        return this.getClass().getAnnotation(Table.class).value();
    }

    protected String getKeyName() {
        return Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Column.class))
                .filter(method -> method.getAnnotation(Column.class).key())
                .findFirst()
                .map(method -> method.getAnnotation(Column.class).value())
                .orElse(null);
    }

    protected Object getKeyValue() {
        return getValue(getKeyName());
    }

    protected List<String> getColumnNames() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> field.getAnnotation(Column.class).value())
                .collect(Collectors.toList());
    }

    protected List<Value> getValues() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> getValue(field.getName()))
                .collect(Collectors.toList());
    }

    private Value getValue(String name) {
        try {
            return Value.of(this.getClass().getMethod("get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name)).invoke(this));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, Value> entityToMap() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> new AbstractMap.SimpleEntry<>(field.getName(), getValue(field.getName())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings({"unchecked", "JavaReflectionInvocation"})
    protected T mapToEntity(Map<String, Object> result) {
        try {
            Record<T> record = new Record<>();
            for (Field field : Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.isAnnotationPresent(Column.class))
                    .collect(Collectors.toList())) {
                Method method = record.getClass().getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field.getName()));
                method.invoke(record, result.get(field.getName()) != null ? method.getParameters()[0].getType().cast(result.get(field.getName())) : null);
            }
            return (T) record;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
        Map<String, Object> params = new HashMap<>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("conditionals", conditionals);
            put("sorts", sorts);
            put("limit", limit);
            put("offset", offset);
        }};
        try {
            return new JDBCAdapter<T>("stg/record/select.stg", "select", params) {
                @Override
                protected T map(Map<String, Object> result) {
                    return mapToEntity(result);
                }
            }.queryList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        Map<String, Object> params = new HashMap<>() {{
            put("table", getTableName());
            put("conditionals", conditionals);
        }};
        try {
            return new JDBCAdapter<Integer>("stg/record/select.stg", "selectCount", params) {
                @Override
                protected Integer map(Map<String, Object> result) {
                    return (Integer) result.values().iterator().next();
                }
            }.query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists() {
        Map<String, Object> params = new HashMap<>() {{
            put("table", getTableName());
            put("conditionals", conditionals);
        }};
        try {
            return new JDBCAdapter<Boolean>("stg/record/select.stg", "selectExist", params) {
                @Override
                protected Boolean map(Map<String, Object> result) {
                    return result.values().iterator().hasNext();
                }
            }.query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public T insert() {
        Map<String, Object> params = new HashMap<>() {{
            put("table", getTableName());
            put("columns", getColumnNames());
            put("values", getValues());
        }};
        try {
            new JDBCAdapter<T>("stg/record/insert.stg", "insert", params) {
                @Override
                protected T map(Map<String, Object> result) {
                    return mapToEntity(result);
                }
            }.update();
            where(this, EQ(getKeyName(), LAST_INSERT_ID));
            return first();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> insertAll(Record<T>... records) {
        Record<T> record = new Record<>();
        Map<String, Object> params = new HashMap<>() {{
            put("records", records);
        }};
        try {
            new JDBCAdapter<T>("stg/record/insert.stg", "insertAll", params) {
                @Override
                protected T map(Map<String, Object> result) {
                    return record.mapToEntity(result);
                }
            }.update();
            where(record, GTE(record.getKeyName(), LAST_INSERT_ID));
            return record.list();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public T update() {
        return update(getKeyValue(), entityToMap().entrySet().stream().map(entry -> SET(entry.getKey(), entry.getValue())).toArray(ValueSet[]::new));

    }

    public static <T> T update(Object value, ValueSet... sets) {
        Record<T> record = new Record<>();
        where(record, EQ(record.getKeyName(), value));
        record.updateAll(sets);
        return record.first();
    }

    public boolean updateAll(ValueSet... sets) {
        Map<String, Object> params = new HashMap<>() {{
            put("table", getTableName());
            put("sets", sets);
            put("conditionals", conditionals);
        }};
        try {
            return new JDBCAdapter<Integer>("stg/record/update.stg", "update", params) {
                @Override
                protected Integer map(Map<String, Object> result) {
                    return (Integer) result.values().iterator().next();
                }
            }.update() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> updateAll(Record<T>... records) {
        Record<T> record = new Record<>();
        Map<String, Object> params = new HashMap<>() {{
            put("records", records);
        }};
        try {
            new JDBCAdapter<T>("stg/record/update.stg", "updateAll", params) {
                @Override
                protected T map(Map<String, Object> result) {
                    return record.mapToEntity(result);
                }
            }.update();
            where(record, IN(record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList())));
            return record.list();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        Map<String, Object> params = new HashMap<>() {{
            put("table", getTableName());
            put("conditionals", conditionals);
        }};
        try {
            return new JDBCAdapter<Integer>("stg/record/delete.stg", "delete", params) {
                @Override
                protected Integer map(Map<String, Object> result) {
                    return (Integer) result.values().iterator().next();
                }
            }.update();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> long deleteAll(Record<T>... records) {
        Record<T> record = new Record<>();
        where(record, IN(record.getKeyName(), Arrays.stream(records).map(Record::getKeyValue).collect(Collectors.toList())));
        Map<String, Object> params = new HashMap<>() {{
            put("table", record.getTableName());
            put("conditionals", record.getConditionals());
        }};
        try {
            return new JDBCAdapter<Integer>("stg/record/delete.stg", "delete", params) {
                @Override
                protected Integer map(Map<String, Object> result) {
                    return (Integer) result.values().iterator().next();
                }
            }.update();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
