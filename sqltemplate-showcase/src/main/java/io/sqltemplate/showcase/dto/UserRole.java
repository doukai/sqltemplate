package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;

public class UserRole extends Record<UserRole> {
  private Integer id;

  private Integer userId;

  private Integer roleId;

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
    return "user_role";
  }

  @Override
  public String[] getKeyNames() {
    return new String[]{ "id" };
  }

  @Override
  public String[] getColumnNames() {
    return new String[]{ "id","user_id","role_id","is_deprecated","version","realm_id","create_user_id","create_time","update_user_id","update_time","create_group_id","__typename" };
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

  public String getRealmId() {
    return this.realmId;
  }

  public UserRole setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public UserRole setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public UserRole setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public UserRole setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public UserRole setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public UserRole setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public UserRole setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
