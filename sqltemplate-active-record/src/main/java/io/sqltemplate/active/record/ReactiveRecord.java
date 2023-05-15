package io.sqltemplate.active.record;

import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.update.ValueSet;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.sort.DESC.DESC;

public class ReactiveRecord<T> extends TableRecord<T> {

    static public <T> Mono<T> get(Object value) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        where(record, EQ(record.getKeyName(), value));
        return record.first();
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
        }};
        return new R2DBCAdapter<T>("stg/record/select.stg", "select", params, getTxType(), getRollbackOn(), getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return mapToEntity(result);
            }
        }
                .queryList();
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
            orderBy(DESC(getKeyName()));
        } else {
            orderBy(Arrays.stream(fileNames).map(DESC::DESC).collect(Collectors.toList()));
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
        }
                .query();
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
        }
                .query();
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
        }
                .update()
                .doOnSuccess(count -> where(this, EQ(getKeyName(), LAST_INSERT_ID)))
                .then(first());
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
        }
                .update()
                .doOnSuccess(count -> where(record, GTE(record.getKeyName(), LAST_INSERT_ID)))
                .then(record.list());
    }

    public Mono<T> update() {
        return update(getKeyValue(), getValueSets().toArray(new ValueSet[]{}));
    }

    public static <T> Mono<T> update(Object value, ValueSet... sets) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        where(record, EQ(record.getKeyName(), value));
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
        }
                .update().map(count -> (long) count > 0);
    }

    public static <T> Mono<List<T>> updateAll(ReactiveRecord<T>... records) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("records", records);
        }};
        return new R2DBCAdapter<T>("stg/record/update.stg", "updateAll", params, record.getTxType(), record.getRollbackOn(), record.getDontRollbackOn()) {
            @Override
            protected T map(Map<String, Object> result) {
                return record.mapToEntity(result);
            }
        }
                .update()
                .doOnSuccess(count -> where(record, IN(record.getKeyName(), Arrays.stream(records).map(ReactiveRecord::getKeyValue).collect(Collectors.toList()))))
                .then(record.list());
    }

    public Mono<Boolean> delete() {
        return delete(getKeyValue());
    }

    public static <T> Mono<Boolean> delete(Object value) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        where(record, EQ(record.getKeyName(), value));
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
        }
                .update();
    }

    public static <T> Mono<Long> deleteAll(ReactiveRecord<T>... records) {
        ReactiveRecord<T> record = new ReactiveRecord<>();
        where(record, IN(record.getKeyName(), Arrays.stream(records).map(ReactiveRecord::getKeyValue).collect(Collectors.toList())));
        return record.deleteAll();
    }
}
