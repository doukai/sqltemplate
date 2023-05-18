package io.sqltemplate.active.record;

import io.sqltemplate.active.record.model.conditional.AND;
import io.sqltemplate.active.record.model.conditional.Conditional;
import io.sqltemplate.active.record.model.conditional.EQ;
import io.sqltemplate.active.record.model.conditional.GT;
import io.sqltemplate.active.record.model.conditional.GTE;
import io.sqltemplate.active.record.model.conditional.IN;
import io.sqltemplate.active.record.model.conditional.LK;
import io.sqltemplate.active.record.model.conditional.LT;
import io.sqltemplate.active.record.model.conditional.LTE;
import io.sqltemplate.active.record.model.conditional.NEQ;
import io.sqltemplate.active.record.model.conditional.NIL;
import io.sqltemplate.active.record.model.conditional.NIN;
import io.sqltemplate.active.record.model.conditional.NLK;
import io.sqltemplate.active.record.model.conditional.NNIL;
import io.sqltemplate.active.record.model.conditional.OR;
import io.sqltemplate.active.record.model.expression.Expression;
import io.sqltemplate.active.record.model.join.JoinColumn;
import io.sqltemplate.active.record.model.sort.ASC;
import io.sqltemplate.active.record.model.sort.DESC;
import io.sqltemplate.active.record.model.sort.Sort;
import io.sqltemplate.active.record.model.update.ValueSet;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.sqltemplate.active.record.model.expression.Function.LAST_INSERT_ID;
import static io.sqltemplate.active.record.model.update.ValueSet.set;

public class TableRecord<T> {

    public static String DEFAULT_ALIAS = "t";

    private Transactional.TxType txType = Transactional.TxType.REQUIRED;
    private Class<?>[] rollbackOn = {};
    private Class<?>[] dontRollbackOn = {};

    private String alias = DEFAULT_ALIAS;
    private String joinAlias = "j";
    private Record<?> joinRecord;
    private List<Conditional> conditionals;
    private List<Sort> sorts;
    private Integer limit;
    private Integer offset;
    private boolean autoIncrement = false;

    public TableRecord() {
    }

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

    public String getAlias() {
        return alias;
    }

    public TableRecord<T> setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public TableRecord<T> setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
        return this;
    }

    public Record<?> getJoinRecord() {
        return joinRecord;
    }

    public TableRecord<T> setJoinRecord(Record<?> joinRecord) {
        this.joinRecord = joinRecord;
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

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public TableRecord<T> setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }

    public List<JoinColumn> getJoinColumns(String tableName) {
        throw new RuntimeException("join column names undefined: " + tableName);
    }

    public List<JoinColumn> getJoinColumns() {
        throw new RuntimeException("join column names undefined");
    }

    public List<JoinColumn> getInverseJoinColumns() {
        throw new RuntimeException("inverse join column names undefined");
    }

    protected String getTableName() {
        throw new RuntimeException("table name undefined");
    }

    protected List<String> getKeyNames() {
        throw new RuntimeException("key name undefined");
    }

    protected List<String> getColumnNames() {
        throw new RuntimeException("column names undefined");
    }

    protected List<Object> getValues() {
        throw new RuntimeException("values undefined");
    }

    protected Expression getValue(String name) {
        throw new RuntimeException("value undefined: " + name);
    }

    protected Map<String, Expression> entityToMap() {
        throw new RuntimeException("entity to map undefined");
    }

    protected T mapToEntity(Map<String, Object> result) {
        throw new RuntimeException("map to entity undefined");
    }

    protected EQ[] getKeyEQValues() {
        return getKeyNames().stream().map(name -> EQ.eq(name, getValue(name))).toArray(EQ[]::new);
    }

    protected Conditional getKeyEQValues(TableRecord<?>... records) {
        return OR.or(Arrays.stream(records).map(TableRecord::getKeyEQValues).map(AND::and).toArray(AND[]::new));
    }

    protected EQ[] getInsertKeyEQValues() {
        if (isAutoIncrement()) {
            return new EQ[]{EQ.eq(getKeyNames().get(0), LAST_INSERT_ID)};
        } else {
            return getKeyNames().stream().map(name -> EQ.eq(name, getValue(name))).toArray(EQ[]::new);
        }
    }

    protected Conditional getInsertKeyEQValues(TableRecord<?>... records) {
        if (isAutoIncrement()) {
            return GTE.gte(getKeyNames().get(0), LAST_INSERT_ID);
        } else {
            return OR.or(Arrays.stream(records).map(TableRecord::getKeyEQValues).map(AND::and).toArray(AND[]::new));
        }
    }

    protected EQ[] getKeyEQValues(Object... values) {
        return IntStream.range(0, getKeyNames().size()).mapToObj(index -> EQ.eq(getKeyNames().get(index), values[index])).toArray(EQ[]::new);
    }

    protected List<Object> getKeyValues() {
        return getKeyNames().stream().map(this::getValue).collect(Collectors.toList());
    }

    protected List<Expression> getValueExpressions() {
        return getValues().stream().map(Expression::of).collect(Collectors.toList());
    }

    protected List<ValueSet> getValueSets() {
        return entityToMap().entrySet().stream().map(entry -> set(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    public TableRecord<T> and(Conditional conditional) {
        if (this.conditionals == null) {
            this.conditionals = new ArrayList<>();
        }
        this.conditionals.add(conditional);
        return this;
    }

    public TableRecord<T> on(List<JoinColumn> joinColumns) {
        joinColumns.forEach(joinColumn -> this.and(EQ.eq(joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))));
        return this;
    }

    public <J> TableRecord<T> on(Record<J> joinRecord) {
        this.setJoinRecord(joinRecord);
        joinRecord.getInverseJoinColumns().forEach(joinColumn -> this.and(EQ.eq(getJoinAlias(), joinColumn.getReferencedColumnName(), getValue(joinColumn.getName()))));
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

    public static <T> TableRecord<T> where(Conditional... conditionals) {
        TableRecord<T> record = new TableRecord<>();
        return where(record, conditionals);
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

    public static <T> TableRecord<T> where(TableRecord<T> record, Conditional... conditionals) {
        record.setConditionals(new ArrayList<>());
        record.getConditionals().addAll(Arrays.asList(conditionals));
        return record;
    }

    public static <T> TableRecord<T> where(TableRecord<T> record) {
        record.setConditionals(new ArrayList<>());
        return record;
    }

    public TableRecord<T> eq(String columnName, Object expression) {
        getConditionals().add(EQ.eq(columnName, expression));
        return this;
    }

    public TableRecord<T> neq(String columnName, Object expression) {
        getConditionals().add(NEQ.neq(columnName, expression));
        return this;
    }

    public TableRecord<T> gt(String columnName, Object expression) {
        getConditionals().add(GT.gt(columnName, expression));
        return this;
    }

    public TableRecord<T> gte(String columnName, Object expression) {
        getConditionals().add(GTE.gte(columnName, expression));
        return this;
    }

    public TableRecord<T> lt(String columnName, Object expression) {
        getConditionals().add(LT.lt(columnName, expression));
        return this;
    }

    public TableRecord<T> lte(String columnName, Object expression) {
        getConditionals().add(LTE.lte(columnName, expression));
        return this;
    }

    public TableRecord<T> lk(String columnName, Object expression) {
        getConditionals().add(LK.lk(columnName, expression));
        return this;
    }

    public TableRecord<T> nlk(String columnName, Object expression) {
        getConditionals().add(NLK.nlk(columnName, expression));
        return this;
    }

    public TableRecord<T> nil(String columnName) {
        getConditionals().add(NIL.nil(columnName));
        return this;
    }

    public TableRecord<T> nnil(String columnName) {
        getConditionals().add(NNIL.nnil(columnName));
        return this;
    }

    public TableRecord<T> in(String columnName, Collection<Object> expressions) {
        getConditionals().add(IN.in(columnName, expressions));
        return this;
    }

    public TableRecord<T> nin(String columnName, Collection<Object> expressions) {
        getConditionals().add(NIN.nin(columnName, expressions));
        return this;
    }

    public TableRecord<T> in(String columnName, Object... expressions) {
        getConditionals().add(IN.in(columnName, expressions));
        return this;
    }

    public TableRecord<T> nin(String columnName, Object... expressions) {
        getConditionals().add(NIN.nin(columnName, expressions));
        return this;
    }

    public TableRecord<T> or(Function<TableRecord<T>, TableRecord<T>> orConditionBuilder) {
        TableRecord<T> record = new TableRecord<>();
        TableRecord<T> result = orConditionBuilder.apply(record);
        getConditionals().add(OR.or(result.getConditionals()));
        return this;
    }

    public TableRecord<T> or(Collection<Conditional> conditionals) {
        getConditionals().add(OR.or(conditionals));
        return this;
    }

    public TableRecord<T> or(Conditional... conditionals) {
        getConditionals().add(OR.or(conditionals));
        return this;
    }

    public TableRecord<T> asc(String columnName) {
        getSorts().add(ASC.asc(columnName));
        return this;
    }

    public TableRecord<T> desc(String columnName) {
        getSorts().add(DESC.desc(columnName));
        return this;
    }
}
