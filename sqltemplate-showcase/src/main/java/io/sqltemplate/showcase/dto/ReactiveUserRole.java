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
    name = "user_role"
)
public class ReactiveUserRole extends ReactiveRecord<ReactiveUserRole> {
  private static final String tableName = "user_role";

  private static final String[] keyNames = new String[]{"id"};

  private static final String[] columnNames = new String[]{"id", "user_id", "role_id", "is_deprecated", "version"};

  private static final Boolean autoIncrement = true;

  private Integer id;

  private Integer userId;

  private Integer roleId;

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
    } else if (columnName.equals("user_id")) {
      return getUserId();
    } else if (columnName.equals("role_id")) {
      return getRoleId();
    } else if (columnName.equals("is_deprecated")) {
      return getIsDeprecated();
    } else if (columnName.equals("version")) {
      return getVersion();
    }
    return null;
  }

  @Override
  public ReactiveUserRole mapToEntity(Map<String, Object> result) {
    ReactiveUserRole entity = new ReactiveUserRole();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setUserId(result.get("userId") != null ? (Integer) result.get("userId") : null);
    entity.setRoleId(result.get("roleId") != null ? (Integer) result.get("roleId") : null);
    entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
    entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
    return entity;
  }

  @Override
  public Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public static Mono<ReactiveUserRole> get(Object... values) {
    return get(tableName, values);
  }

  public static Mono<List<ReactiveUserRole>> all() {
    return all(tableName);
  }

  public static Mono<ReactiveUserRole> firstOfAll() {
    return firstOfAll(tableName);
  }

  public static Mono<ReactiveUserRole> lastOfAll(Object... columnNames) {
    return lastOfAll(tableName, columnNames);
  }

  public static Mono<Long> allCount() {
    return allCount(tableName);
  }

  public static Mono<List<ReactiveUserRole>> insertAll(ReactiveUserRole... records) {
    return insertAll(tableName, records);
  }

  public static Mono<List<ReactiveUserRole>> updateAll(ReactiveUserRole... records) {
    return updateAll(tableName, records);
  }

  public static Mono<Long> deleteAll(ReactiveUserRole... records) {
    return deleteAll(tableName, records);
  }

  public static ReactiveUserRole where() {
    return where(tableName);
  }

  public static ReactiveUserRole where(Conditional conditional) {
    return where(tableName, conditional);
  }

  public static ReactiveUserRole where(Conditional... conditionals) {
    return where(tableName, conditionals);
  }

  public static ReactiveUserRole record() {
    return record(tableName);
  }

  public Integer getId() {
    return this.id;
  }

  public ReactiveUserRole setId(Integer id) {
    this.id = id;
    return this;
  }

  public Integer getUserId() {
    return this.userId;
  }

  public ReactiveUserRole setUserId(Integer userId) {
    this.userId = userId;
    return this;
  }

  public Integer getRoleId() {
    return this.roleId;
  }

  public ReactiveUserRole setRoleId(Integer roleId) {
    this.roleId = roleId;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public ReactiveUserRole setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public ReactiveUserRole setVersion(Integer version) {
    this.version = version;
    return this;
  }
}
