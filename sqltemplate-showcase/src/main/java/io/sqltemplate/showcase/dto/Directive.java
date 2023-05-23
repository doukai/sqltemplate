package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class Directive extends Record<Directive> {
  private String name;

  private Integer schemaId;

  private String description;

  private Boolean isRepeatable;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  private final String tableName = "__directive";

  private final String[] keyNames = new String[]{"name"};

  private final String[] columnNames = new String[]{"name", "schema_id", "description", "is_repeatable", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

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
    if (columnName.equals("name")) {
      return getName();
    } else if (columnName.equals("schema_id")) {
      return getSchemaId();
    } else if (columnName.equals("description")) {
      return getDescription();
    } else if (columnName.equals("is_repeatable")) {
      return getIsRepeatable();
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
  protected Directive mapToEntity(Map<String, Object> result) {
    Directive entity = new Directive();
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setSchemaId(result.get("schemaId") != null ? (Integer) result.get("schemaId") : null);
    entity.setDescription(result.get("description") != null ? (String) result.get("description") : null);
    entity.setIsRepeatable(result.get("isRepeatable") != null ? (Boolean) result.get("isRepeatable") : null);
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
  protected Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public String getName() {
    return this.name;
  }

  public Directive setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getSchemaId() {
    return this.schemaId;
  }

  public Directive setSchemaId(Integer schemaId) {
    this.schemaId = schemaId;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public Directive setDescription(String description) {
    this.description = description;
    return this;
  }

  public Boolean getIsRepeatable() {
    return this.isRepeatable;
  }

  public Directive setIsRepeatable(Boolean isRepeatable) {
    this.isRepeatable = isRepeatable;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Directive setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Directive setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public Directive setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Directive setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Directive setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Directive setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Directive setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Directive setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Directive setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
