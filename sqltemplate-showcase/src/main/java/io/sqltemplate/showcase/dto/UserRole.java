package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import io.sqltemplate.active.record.model.conditional.Conditional;
import jakarta.annotation.Generated;
import jakarta.persistence.Table;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Map;

@Generated("io.sqltemplate.gradle.task.GenerateRecord")
@Table(
    name = "user_role"
)
public class UserRole extends Record<UserRole> {
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
  public UserRole mapToEntity(Map<String, Object> result) {
    UserRole entity = new UserRole();
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

  public static UserRole get(Object... values) {
    return get(tableName, values);
  }

  public static List<UserRole> all() {
    return all(tableName);
  }

  public static UserRole firstOfAll() {
    return firstOfAll(tableName);
  }

  public static UserRole lastOfAll(Object... columnNames) {
    return lastOfAll(tableName, columnNames);
  }

  public static long allCount() {
    return allCount(tableName);
  }

  public static List<UserRole> insertAll(UserRole... records) {
    return insertAll(tableName, records);
  }

  public static List<UserRole> updateAll(UserRole... records) {
    return updateAll(tableName, records);
  }

  public static long deleteAll(UserRole... records) {
    return deleteAll(tableName, records);
  }

  public static UserRole where() {
    return where(tableName);
  }

  public static UserRole where(Conditional conditional) {
    return where(tableName, conditional);
  }

  public static UserRole where(Conditional... conditionals) {
    return where(tableName, conditionals);
  }

  public static UserRole record() {
    return record(tableName);
  }

  public Integer getId() {
    return this.id;
  }

  public UserRole setId(Integer id) {
    this.id = id;
    return this;
  }

  public Integer getUserId() {
    return this.userId;
  }

  public UserRole setUserId(Integer userId) {
    this.userId = userId;
    return this;
  }

  public Integer getRoleId() {
    return this.roleId;
  }

  public UserRole setRoleId(Integer roleId) {
    this.roleId = roleId;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public UserRole setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public UserRole setVersion(Integer version) {
    this.version = version;
    return this;
  }
}
