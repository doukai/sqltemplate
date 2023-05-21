package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class Type extends Record<Type> {
  private String name;

  private Integer schemaId;

  private String kind;

  private String description;

  private String ofTypeName;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  private final String tableName = "__type";

  private final String[] keyNames = new String[]{"name"};

  private final String[] columnNames = new String[]{"name", "schema_id", "kind", "description", "of_type_name", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

  private final Boolean autoIncrement = false;

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
    if (columnName.equals("name")) {
      return getName();
    } else if (columnName.equals("schema_id")) {
      return getSchemaId();
    } else if (columnName.equals("kind")) {
      return getKind();
    } else if (columnName.equals("description")) {
      return getDescription();
    } else if (columnName.equals("of_type_name")) {
      return getOfTypeName();
    } else if (columnName.equals("is_deprecated")) {
      return getIsDeprecated();
    } else if (columnName.equals("version")) {
      return getVersion();
    } else if (columnName.equals("realm_id")) {
      return getRealmId();
    } else if (columnName.equals("create_user_id")) {
      return getCreateUserId();
    } else if (columnName.equals("create_time")) {
      return getCreateTime();
    } else if (columnName.equals("update_user_id")) {
      return getUpdateUserId();
    } else if (columnName.equals("update_time")) {
      return getUpdateTime();
    } else if (columnName.equals("create_group_id")) {
      return getCreateGroupId();
    } else if (columnName.equals("__typename")) {
      return getTypename();
    }
    return null;
  }

  @Override
  public Type mapToEntity(Map<String, Object> result) {
    Type entity = new Type();
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setSchemaId(result.get("schemaId") != null ? (Integer) result.get("schemaId") : null);
    entity.setKind(result.get("kind") != null ? (String) result.get("kind") : null);
    entity.setDescription(result.get("description") != null ? (String) result.get("description") : null);
    entity.setOfTypeName(result.get("ofTypeName") != null ? (String) result.get("ofTypeName") : null);
    entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
    entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
    entity.setRealmId(result.get("realmId") != null ? (String) result.get("realmId") : null);
    entity.setCreateUserId(result.get("createUserId") != null ? (String) result.get("createUserId") : null);
    entity.setCreateTime(result.get("createTime") != null ? (LocalDateTime) result.get("createTime") : null);
    entity.setUpdateUserId(result.get("updateUserId") != null ? (String) result.get("updateUserId") : null);
    entity.setUpdateTime(result.get("updateTime") != null ? (LocalDateTime) result.get("updateTime") : null);
    entity.setCreateGroupId(result.get("createGroupId") != null ? (String) result.get("createGroupId") : null);
    entity.setTypename(result.get("Typename") != null ? (String) result.get("Typename") : null);
    return entity;
  }

  @Override
  public Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public String getName() {
    return this.name;
  }

  public Type setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getSchemaId() {
    return this.schemaId;
  }

  public Type setSchemaId(Integer schemaId) {
    this.schemaId = schemaId;
    return this;
  }

  public String getKind() {
    return this.kind;
  }

  public Type setKind(String kind) {
    this.kind = kind;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public Type setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getOfTypeName() {
    return this.ofTypeName;
  }

  public Type setOfTypeName(String ofTypeName) {
    this.ofTypeName = ofTypeName;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Type setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Type setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public Type setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Type setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Type setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Type setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Type setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Type setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Type setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
