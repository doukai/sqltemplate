package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.ReactiveRecord;
import io.sqltemplate.active.record.model.conditional.Conditional;
import jakarta.annotation.Generated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
        name = "user"
)
public class ReactiveUser extends ReactiveRecord<ReactiveUser> {
    private static final String tableName = "user";

    private static final String[] keyNames = new String[]{"id"};

    private static final String[] columnNames = new String[]{"id", "login", "password", "name", "age", "disable", "sex", "organization_id", "is_deprecated", "version"};

    private static final Boolean autoIncrement = false;

    private Integer id;

    private String login;

    private String password;

    private String name;

    private Integer age;

    private Boolean disable;

    private String sex;

    private Integer organizationId;

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
        } else if (columnName.equals("login")) {
            return getLogin();
        } else if (columnName.equals("password")) {
            return getPassword();
        } else if (columnName.equals("name")) {
            return getName();
        } else if (columnName.equals("age")) {
            return getAge();
        } else if (columnName.equals("disable")) {
            return getDisable();
        } else if (columnName.equals("sex")) {
            return getSex();
        } else if (columnName.equals("organization_id")) {
            return getOrganizationId();
        } else if (columnName.equals("is_deprecated")) {
            return getIsDeprecated();
        } else if (columnName.equals("version")) {
            return getVersion();
        }
        return null;
    }

    @Override
    public ReactiveUser mapToEntity(Map<String, Object> result) {
        ReactiveUser entity = new ReactiveUser();
        entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
        entity.setLogin(result.get("login") != null ? (String) result.get("login") : null);
        entity.setPassword(result.get("password") != null ? (String) result.get("password") : null);
        entity.setName(result.get("name") != null ? (String) result.get("name") : null);
        entity.setAge(result.get("age") != null ? (Integer) result.get("age") : null);
        entity.setDisable(result.get("disable") != null ? (Boolean) result.get("disable") : null);
        entity.setSex(result.get("sex") != null ? (String) result.get("sex") : null);
        entity.setOrganizationId(result.get("organizationId") != null ? (Integer) result.get("organizationId") : null);
        entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
        entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
        return entity;
    }

    @Override
    public Boolean isAutoIncrement() {
        return autoIncrement;
    }

    public static Mono<ReactiveUser> get(Object... values) {
        return get(tableName, values);
    }

    public static Mono<List<ReactiveUser>> all() {
        return all(tableName);
    }

    public static Mono<ReactiveUser> firstOfAll() {
        return firstOfAll(tableName);
    }

    public static Mono<ReactiveUser> lastOfAll(Object... columnNames) {
        return lastOfAll(tableName, columnNames);
    }

    public static Mono<Long> allCount() {
        return allCount(tableName);
    }

    public static Mono<List<ReactiveUser>> insertAll(ReactiveUser... records) {
        return insertAll(tableName, records);
    }

    public static Mono<List<ReactiveUser>> updateAll(ReactiveUser... records) {
        return updateAll(tableName, records);
    }

    public static Mono<Long> deleteAll(ReactiveUser... records) {
        return deleteAll(tableName, records);
    }

    public static ReactiveUser where() {
        return where(tableName);
    }

    public static ReactiveUser where(Conditional conditional) {
        return where(tableName, conditional);
    }

    public static ReactiveUser where(Conditional... conditionals) {
        return where(tableName, conditionals);
    }

    public static ReactiveUser record() {
        return record(tableName);
    }

    public Integer getId() {
        return this.id;
    }

    public ReactiveUser setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return this.login;
    }

    public ReactiveUser setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public ReactiveUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ReactiveUser setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return this.age;
    }

    public ReactiveUser setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Boolean getDisable() {
        return this.disable;
    }

    public ReactiveUser setDisable(Boolean disable) {
        this.disable = disable;
        return this;
    }

    public String getSex() {
        return this.sex;
    }

    public ReactiveUser setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public Integer getOrganizationId() {
        return this.organizationId;
    }

    public ReactiveUser setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Boolean getIsDeprecated() {
        return this.isDeprecated;
    }

    public ReactiveUser setIsDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
        return this;
    }

    public Integer getVersion() {
        return this.version;
    }

    public ReactiveUser setVersion(Integer version) {
        this.version = version;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    public Mono<ReactiveOrganization> getOrganization() {
        return getOne(ReactiveOrganization.class);
    }

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )

    public Mono<List<ReactiveRole>> getRoleList() {
        return getManyByJoin(ReactiveRole.class);
    }

    public Mono<List<ReactiveRole>> addRoleList(List<ReactiveRole> roleList) {
        return addManyByJoin(ReactiveRole.class, roleList.toArray(new ReactiveRole[]{}));
    }

    public Mono<Long> removeRoleList(List<ReactiveRole> roleList) {
        return removeManyByJoin(ReactiveRole.class, roleList.toArray(new ReactiveRole[]{}));
    }
}
