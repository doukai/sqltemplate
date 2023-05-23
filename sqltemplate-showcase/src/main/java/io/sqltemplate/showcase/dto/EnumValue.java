package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class EnumValue extends Record<EnumValue> {
  private Integer id;

  private String name;

  private String ofTypeName;

  private String description;

  private String deprecationReason;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  private final String tableName = "__enum_value";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "name", "of_type_name", "description", "deprecation_reason", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

  private final Boolean autoIncrement = true;

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
    } else if (columnName.equals("name")) {
      return getName();
    } else if (columnName.equals("of_type_name")) {
      return getOfTypeName();
    } else if (columnName.equals("description")) {
      return getDescription();
    } else if (columnName.equals("deprecation_reason")) {
      return getDeprecationReason();
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
  protected EnumValue mapToEntity(Map<String, Object> result) {
    EnumValue entity = new EnumValue();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setOfTypeName(result.get("ofTypeName") != null ? (String) result.get("ofTypeName") : null);
    entity.setDescription(result.get("description") != null ? (String) result.get("description") : null);
    entity.setDeprecationReason(result.get("deprecationReason") != null ? (String) result.get("deprecationReason") : null);
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

  public Integer getId() {
    return this.id;
  }

  public EnumValue setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public EnumValue setName(String name) {
    this.name = name;
    return this;
  }

  public String getOfTypeName() {
    return this.ofTypeName;
  }

  public EnumValue setOfTypeName(String ofTypeName) {
    this.ofTypeName = ofTypeName;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public EnumValue setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getDeprecationReason() {
    return this.deprecationReason;
  }

  public EnumValue setDeprecationReason(String deprecationReason) {
    this.deprecationReason = deprecationReason;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public EnumValue setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public EnumValue setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public EnumValue setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public EnumValue setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public EnumValue setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public EnumValue setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public EnumValue setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public EnumValue setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public EnumValue setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
