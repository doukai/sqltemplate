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
    name = "organization"
)
public class Organization extends Record<Organization> {
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
  public Organization mapToEntity(Map<String, Object> result) {
    Organization entity = new Organization();
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

  public static Organization get(Object... values) {
    return get(tableName, values);
  }

  public static List<Organization> all() {
    return all(tableName);
  }

  public static Organization firstOfAll() {
    return firstOfAll(tableName);
  }

  public static Organization lastOfAll(Object... columnNames) {
    return lastOfAll(tableName, columnNames);
  }

  public static long allCount() {
    return allCount(tableName);
  }

  public static List<Organization> insertAll(Organization... records) {
    return insertAll(tableName, records);
  }

  public static List<Organization> updateAll(Organization... records) {
    return updateAll(tableName, records);
  }

  public static long deleteAll(Organization... records) {
    return deleteAll(tableName, records);
  }

  public static Organization where() {
    return where(tableName);
  }

  public static Organization where(Conditional conditional) {
    return where(tableName, conditional);
  }

  public static Organization where(Conditional... conditionals) {
    return where(tableName, conditionals);
  }

  public static Organization record() {
    return record(tableName);
  }

  public Integer getId() {
    return this.id;
  }

  public Organization setId(Integer id) {
    this.id = id;
    return this;
  }

  public Integer getAboveId() {
    return this.aboveId;
  }

  public Organization setAboveId(Integer aboveId) {
    this.aboveId = aboveId;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public Organization setName(String name) {
    this.name = name;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Organization setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Organization setVersion(Integer version) {
    this.version = version;
    return this;
  }
}
