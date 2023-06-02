package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import jakarta.annotation.Generated;
import jakarta.persistence.Table;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Map;

@Generated("io.sqltemplate.gradle.task.GenerateRecord")
@Table(
    name = "organization"
)
public class Organization extends Record<Organization> {
  private Integer id;

  private Integer aboveId;

  private String name;

  private Boolean isDeprecated;

  private Integer version;

  private final String tableName = "organization";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "above_id", "name", "is_deprecated", "version"};

  private final Boolean autoIncrement = false;

  @Override
  protected String getTableName() {
    return tableName;
  }

  @Override
  protected String[] getKeyNames() {
    return keyNames;
  }

  @Override
  protected String[] getColumnNames() {
    return columnNames;
  }

  @Override
  protected Object getValue(String columnName) {
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
  protected Organization mapToEntity(Map<String, Object> result) {
    Organization entity = new Organization();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setAboveId(result.get("aboveId") != null ? (Integer) result.get("aboveId") : null);
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
    entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
    return entity;
  }

  @Override
  protected Boolean isAutoIncrement() {
    return autoIncrement;
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
