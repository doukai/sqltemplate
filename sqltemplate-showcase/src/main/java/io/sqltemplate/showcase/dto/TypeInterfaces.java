package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;

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

  @Override
  public String getTableName() {
    return "__type_interfaces";
  }

  @Override
  public String[] getKeyNames() {
    return new String[]{ "id" };
  }

  @Override
  public String[] getColumnNames() {
    return new String[]{ "id","type_name","interface_name","is_deprecated","version","realm_id","create_user_id","create_time","update_user_id","update_time","create_group_id","__typename" };
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
