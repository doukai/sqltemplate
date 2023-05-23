package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class TypeInterfaces extends Record<TypeInterfaces> {
  private Integer id;

  private String typeName;

  private String interfaceName;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  private final String tableName = "__type_interfaces";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "type_name", "interface_name", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

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
    } else if (columnName.equals("type_name")) {
      return getTypeName();
    } else if (columnName.equals("interface_name")) {
      return getInterfaceName();
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
  protected TypeInterfaces mapToEntity(Map<String, Object> result) {
    TypeInterfaces entity = new TypeInterfaces();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setTypeName(result.get("typeName") != null ? (String) result.get("typeName") : null);
    entity.setInterfaceName(result.get("interfaceName") != null ? (String) result.get("interfaceName") : null);
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

  public TypeInterfaces setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getTypeName() {
    return this.typeName;
  }

  public TypeInterfaces setTypeName(String typeName) {
    this.typeName = typeName;
    return this;
  }

  public String getInterfaceName() {
    return this.interfaceName;
  }

  public TypeInterfaces setInterfaceName(String interfaceName) {
    this.interfaceName = interfaceName;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public TypeInterfaces setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public TypeInterfaces setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public TypeInterfaces setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public TypeInterfaces setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public TypeInterfaces setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public TypeInterfaces setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public TypeInterfaces setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public TypeInterfaces setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public TypeInterfaces setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
