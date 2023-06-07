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
    name = "role"
)
public class ReactiveRole extends ReactiveRecord<ReactiveRole> {
  private static final String tableName = "role";

  private static final String[] keyNames = new String[]{"id"};

  private static final String[] columnNames = new String[]{"id", "name", "is_deprecated", "version"};

  private static final Boolean autoIncrement = false;

  private Integer id;

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
  public ReactiveRole mapToEntity(Map<String, Object> result) {
    ReactiveRole entity = new ReactiveRole();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
    entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
    return entity;
  }

  @Override
  public Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public static Mono<ReactiveRole> get(Object... values) {
    return get(tableName, values);
  }

  public static Mono<List<ReactiveRole>> all() {
    return all(tableName);
  }

  public static Mono<ReactiveRole> firstOfAll() {
    return firstOfAll(tableName);
  }

  public static Mono<ReactiveRole> lastOfAll(Object... columnNames) {
    return lastOfAll(tableName, columnNames);
  }

  public static Mono<Long> allCount() {
    return allCount(tableName);
  }

  public static Mono<List<ReactiveRole>> insertAll(ReactiveRole... records) {
    return insertAll(tableName, records);
  }

  public static Mono<List<ReactiveRole>> updateAll(ReactiveRole... records) {
    return updateAll(tableName, records);
  }

  public static Mono<Long> deleteAll(ReactiveRole... records) {
    return deleteAll(tableName, records);
  }

  public static ReactiveRole where() {
    return where(tableName);
  }

  public static ReactiveRole where(Conditional conditional) {
    return where(tableName, conditional);
  }

  public static ReactiveRole where(Conditional... conditionals) {
    return where(tableName, conditionals);
  }

  public static ReactiveRole record() {
    return record(tableName);
  }

  public Integer getId() {
    return this.id;
  }

  public ReactiveRole setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public ReactiveRole setName(String name) {
    this.name = name;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public ReactiveRole setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public ReactiveRole setVersion(Integer version) {
    this.version = version;
    return this;
  }
}
