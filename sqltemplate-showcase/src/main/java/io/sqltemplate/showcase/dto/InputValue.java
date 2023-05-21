package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class InputValue extends Record<InputValue> {
  private Integer id;

  private String name;

  private String typeName;

  private String ofTypeName;

  private Integer fieldId;

  private String directiveName;

  private String description;

  private String defaultValue;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  private final String tableName = "__input_value";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "name", "type_name", "of_type_name", "field_id", "directive_name", "description", "default_value", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

  private final Boolean autoIncrement = true;

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
    } else if (columnName.equals("type_name")) {
      return getTypeName();
    } else if (columnName.equals("of_type_name")) {
      return getOfTypeName();
    } else if (columnName.equals("field_id")) {
      return getFieldId();
    } else if (columnName.equals("directive_name")) {
      return getDirectiveName();
    } else if (columnName.equals("description")) {
      return getDescription();
    } else if (columnName.equals("default_value")) {
      return getDefaultValue();
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
  public InputValue mapToEntity(Map<String, Object> result) {
    InputValue entity = new InputValue();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setTypeName(result.get("typeName") != null ? (String) result.get("typeName") : null);
    entity.setOfTypeName(result.get("ofTypeName") != null ? (String) result.get("ofTypeName") : null);
    entity.setFieldId(result.get("fieldId") != null ? (Integer) result.get("fieldId") : null);
    entity.setDirectiveName(result.get("directiveName") != null ? (String) result.get("directiveName") : null);
    entity.setDescription(result.get("description") != null ? (String) result.get("description") : null);
    entity.setDefaultValue(result.get("defaultValue") != null ? (String) result.get("defaultValue") : null);
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

  public Integer getId() {
    return this.id;
  }

  public InputValue setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public InputValue setName(String name) {
    this.name = name;
    return this;
  }

  public String getTypeName() {
    return this.typeName;
  }

  public InputValue setTypeName(String typeName) {
    this.typeName = typeName;
    return this;
  }

  public String getOfTypeName() {
    return this.ofTypeName;
  }

  public InputValue setOfTypeName(String ofTypeName) {
    this.ofTypeName = ofTypeName;
    return this;
  }

  public Integer getFieldId() {
    return this.fieldId;
  }

  public InputValue setFieldId(Integer fieldId) {
    this.fieldId = fieldId;
    return this;
  }

  public String getDirectiveName() {
    return this.directiveName;
  }

  public InputValue setDirectiveName(String directiveName) {
    this.directiveName = directiveName;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public InputValue setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public InputValue setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public InputValue setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public InputValue setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public InputValue setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public InputValue setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public InputValue setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public InputValue setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public InputValue setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public InputValue setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public InputValue setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
