package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.ReactiveRecord;
import io.sqltemplate.active.record.model.conditional.Conditional;
import jakarta.annotation.Generated;
import jakarta.persistence.Table;

import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Map;

import reactor.core.publisher.Mono;

@Generated("io.sqltemplate.gradle.task.GenerateRecord")
@Table(
        name = "organization"
)
public class ReactiveOrganization extends ReactiveRecord<ReactiveOrganization> {
    private static final String tableName = "organization";

    private static final String[] keyNames = new String[]{"id"};

    private static final String[] columnNames = new String[]{"id", "above_id", "name", "is_deprecated", "version"};

    private static final Boolean autoIncrement = false;

    private Integer id;

    private Integer aboveId;

    private String name;

    private Boolean isDeprecated;

    private Integer version;

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String[] getKeyNames() {
        return keyNames;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public Object getValue(String columnName) {
        if (columnName.equals("id")) {
            return getId();
        } else if (columnName.equals("above_id")) {
            return getAboveId();
        } else if (columnName.equals("name")) {
            return getName();
        } else if (columnName.equals("is_deprecated")) {
            return getIsDeprecated();
        } else if (columnName.equals("version")) {
            return getVersion();
        }
        return null;
    }

    @Override
    public ReactiveOrganization mapToEntity(Map<String, Object> result) {
        ReactiveOrganization entity = new ReactiveOrganization();
        entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
        entity.setAboveId(result.get("aboveId") != null ? (Integer) result.get("aboveId") : null);
        entity.setName(result.get("name") != null ? (String) result.get("name") : null);
        entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
        entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
        return entity;
    }

    @Override
    public Boolean isAutoIncrement() {
        return autoIncrement;
    }

    public static Mono<ReactiveOrganization> get(Object... values) {
        return get(tableName, values);
    }

    public static Mono<List<ReactiveOrganization>> all() {
        return all(tableName);
    }

    public static Mono<ReactiveOrganization> firstOfAll() {
        return firstOfAll(tableName);
    }

    public static Mono<ReactiveOrganization> lastOfAll(Object... columnNames) {
        return lastOfAll(tableName, columnNames);
    }

    public static Mono<Long> allCount() {
        return allCount(tableName);
    }

    public static Mono<List<ReactiveOrganization>> insertAll(ReactiveOrganization... records) {
        return insertAll(tableName, records);
    }

    public static Mono<List<ReactiveOrganization>> updateAll(ReactiveOrganization... records) {
        return updateAll(tableName, records);
    }

    public static Mono<Long> deleteAll(ReactiveOrganization... records) {
        return deleteAll(tableName, records);
    }

    public static ReactiveOrganization where() {
        return where(tableName);
    }

    public static ReactiveOrganization where(Conditional conditional) {
        return where(tableName, conditional);
    }

    public static ReactiveOrganization where(Conditional... conditionals) {
        return where(tableName, conditionals);
    }

    public static ReactiveOrganization record() {
        return record(tableName);
    }

    public Integer getId() {
        return this.id;
    }

    public ReactiveOrganization setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getAboveId() {
        return this.aboveId;
    }

    public ReactiveOrganization setAboveId(Integer aboveId) {
        this.aboveId = aboveId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ReactiveOrganization setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getIsDeprecated() {
        return this.isDeprecated;
    }

    public ReactiveOrganization setIsDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
        return this;
    }

    public Integer getVersion() {
        return this.version;
    }

    public ReactiveOrganization setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Mono<List<ReactiveUser>> getUserList() {
        return getMany(ReactiveUser.class);
    }

    public Mono<ReactiveUser> addUser(ReactiveUser user) {
        return addOne(ReactiveUser.class, user);
    }

    public Mono<List<ReactiveUser>> addUserList(List<ReactiveUser> userList) {
        return addMany(ReactiveUser.class, userList.toArray(new ReactiveUser[]{}));
    }

    public Mono<ReactiveUser> removeUser(ReactiveUser user) {
        return removeOne(ReactiveUser.class, user);
    }

    public Mono<List<ReactiveUser>> removeUserList(List<ReactiveUser> userList) {
        return removeMany(ReactiveUser.class, userList.toArray(new ReactiveUser[]{}));
    }
}
