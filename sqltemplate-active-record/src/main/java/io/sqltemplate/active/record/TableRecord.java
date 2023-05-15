package io.sqltemplate.active.record;

import com.google.common.base.CaseFormat;
import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.expression.Expression;
import io.sqltemplate.active.record.model.join.JoinTable;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.sqltemplate.active.record.model.conditional.EQ.EQ;
import static io.sqltemplate.active.record.model.conditional.GT.GT;
import static io.sqltemplate.active.record.model.conditional.GTE.GTE;
import static io.sqltemplate.active.record.model.conditional.LK.LK;
import static io.sqltemplate.active.record.model.conditional.LT.LT;
import static io.sqltemplate.active.record.model.conditional.LTE.LTE;
import static io.sqltemplate.active.record.model.conditional.NEQ.NEQ;
import static io.sqltemplate.active.record.model.conditional.NIL.NIL;
import static io.sqltemplate.active.record.model.conditional.NLK.NLK;
import static io.sqltemplate.active.record.model.conditional.NNIL.NNIL;
import static io.sqltemplate.active.record.model.conditional.IN.IN;
import static io.sqltemplate.active.record.model.conditional.NIN.NIN;
import static io.sqltemplate.active.record.model.conditional.OR.OR;
import static io.sqltemplate.active.record.model.update.ValueSet.SET;

public class TableRecord<T> {

    private Transactional.TxType txType = Transactional.TxType.REQUIRED;
    private Class<?>[] rollbackOn = {};
    private Class<?>[] dontRollbackOn = {};

    private io.sqltemplate.active.record.model.join.JoinTable joinTable;
    private List<Conditional> conditionals;
    private List<Sort> sorts;
    private Integer limit;
    private Integer offset;

    public Transactional.TxType getTxType() {
        return txType;
    }

    public TableRecord<T> setTxType(Transactional.TxType txType) {
        this.txType = txType;
        return this;
    }

    public Class<?>[] getRollbackOn() {
        return rollbackOn;
    }

    public TableRecord<T> setRollbackOn(Class<?>[] rollbackOn) {
        this.rollbackOn = rollbackOn;
        return this;
    }

    public Class<?>[] getDontRollbackOn() {
        return dontRollbackOn;
    }

    public TableRecord<T> setDontRollbackOn(Class<?>[] dontRollbackOn) {
        this.dontRollbackOn = dontRollbackOn;
        return this;
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public TableRecord<T> setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public List<Conditional> getConditionals() {
        return conditionals;
    }

    public TableRecord<T> setConditionals(List<Conditional> conditionals) {
        this.conditionals = conditionals;
        return this;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public TableRecord<T> setSorts(List<Sort> sorts) {
        this.sorts = sorts;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public TableRecord<T> setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public TableRecord<T> setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    protected String getTableName() {
        return this.getClass().getAnnotation(Table.class).name();
    }

    protected String getKeyName() {
        return Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Id.class))
                .filter(method -> method.isAnnotationPresent(Column.class))
                .findFirst()
                .map(method -> method.getAnnotation(Column.class).name())
                .orElse(null);
    }

    protected Object getKeyValue() {
        return getValue(getKeyName());
    }

    protected List<String> getColumnNames() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .filter(field ->
                        !field.isAnnotationPresent(OneToOne.class) &&
                                !field.isAnnotationPresent(OneToMany.class) &&
                                !field.isAnnotationPresent(ManyToOne.class) &&
                                !field.isAnnotationPresent(ManyToMany.class)
                )
                .map(field -> field.getAnnotation(Column.class).name())
                .collect(Collectors.toList());
    }

    protected List<Expression> getValues() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> getValue(field.getName()))
                .collect(Collectors.toList());
    }

    private Expression getValue(String name) {
        try {
            return Expression.of(this.getClass().getMethod("get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name)).invoke(this));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<ValueSet> getValueSets() {
        return entityToMap().entrySet().stream().map(entry -> SET(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    protected Map<String, Expression> entityToMap() {
        return Arrays.stream(this.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> new AbstractMap.SimpleEntry<>(field.getName(), getValue(field.getName())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings({"unchecked", "JavaReflectionInvocation"})
    protected T mapToEntity(Map<String, Object> result) {
        try {
            TableRecord<T> record = new TableRecord<>();
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

    public TableRecord<T> and(Conditional conditional) {
        if (this.conditionals == null) {
            this.conditionals = new ArrayList<>();
        }
        this.conditionals.add(conditional);
        return this;
    }

    public TableRecord<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public TableRecord<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public TableRecord<T> orderBy(Sort sort) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
        return this;
    }

    public TableRecord<T> orderBy(Collection<Sort> sorts) {
        if (this.sorts == null) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.addAll(sorts);
        return this;
    }

    public static <T> TableRecord<T> where(Conditional conditional) {
        TableRecord<T> record = new TableRecord<>();
        return where(record, conditional);
    }

    public static <T> TableRecord<T> where() {
        TableRecord<T> record = new TableRecord<>();
        return where(record);
    }

    public static <T> TableRecord<T> where(TableRecord<T> record, Conditional conditional) {
        record.setConditionals(new ArrayList<>());
        record.getConditionals().add(conditional);
        return record;
    }

    public static <T> TableRecord<T> where(TableRecord<T> record) {
        record.setConditionals(new ArrayList<>());
        return record;
    }

    public TableRecord<T> eq(String columnName, Object expression) {
        getConditionals().add(EQ(columnName, expression));
        return this;
    }

    public TableRecord<T> neq(String columnName, Object expression) {
        getConditionals().add(NEQ(columnName, expression));
        return this;
    }

    public TableRecord<T> gt(String columnName, Object expression) {
        getConditionals().add(GT(columnName, expression));
        return this;
    }

    public TableRecord<T> gte(String columnName, Object expression) {
        getConditionals().add(GTE(columnName, expression));
        return this;
    }

    public TableRecord<T> lt(String columnName, Object expression) {
        getConditionals().add(LT(columnName, expression));
        return this;
    }

    public TableRecord<T> lte(String columnName, Object expression) {
        getConditionals().add(LTE(columnName, expression));
        return this;
    }

    public TableRecord<T> lk(String columnName, Object expression) {
        getConditionals().add(LK(columnName, expression));
        return this;
    }

    public TableRecord<T> nlk(String columnName, Object expression) {
        getConditionals().add(NLK(columnName, expression));
        return this;
    }

    public TableRecord<T> nil(String columnName) {
        getConditionals().add(NIL(columnName));
        return this;
    }

    public TableRecord<T> nnil(String columnName) {
        getConditionals().add(NNIL(columnName));
        return this;
    }

    public TableRecord<T> in(String columnName, Collection<Object> expressions) {
        getConditionals().add(IN(columnName, expressions));
        return this;
    }

    public TableRecord<T> nin(String columnName, Collection<Object> expressions) {
        getConditionals().add(NIN(columnName, expressions));
        return this;
    }

    public TableRecord<T> in(String columnName, Object... expressions) {
        getConditionals().add(IN(columnName, expressions));
        return this;
    }

    public TableRecord<T> nin(String columnName, Object... expressions) {
        getConditionals().add(NIN(columnName, expressions));
        return this;
    }

    public TableRecord<T> or(Collection<Conditional> conditionals) {
        getConditionals().add(OR(conditionals));
        return this;
    }

    public TableRecord<T> or(Conditional... conditionals) {
        getConditionals().add(OR(conditionals));
        return this;
    }
}
