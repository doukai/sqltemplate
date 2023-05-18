package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;

public class RoleRoleType extends Record<RoleRoleType> {
  private Integer id;

  private Integer roleId;

  private String type;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  public Integer getId() {
    return this.id;
  }

  public RoleRoleType setId(Integer id) {
    this.id = id;
    return this;
  }

  public Integer getRoleId() {
    return this.roleId;
  }

  public RoleRoleType setRoleId(Integer roleId) {
    this.roleId = roleId;
    return this;
  }

  public String getType() {
    return this.type;
  }

  public RoleRoleType setType(String type) {
    this.type = type;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public RoleRoleType setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public RoleRoleType setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public RoleRoleType setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public RoleRoleType setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public RoleRoleType setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public RoleRoleType setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public RoleRoleType setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public RoleRoleType setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public RoleRoleType setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
