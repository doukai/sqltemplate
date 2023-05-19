package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;

public class Organization extends Record<Organization> {
  private Integer id;

  private String name;

  private Integer aboveId;

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
    return "organization";
  }

  @Override
  public String[] getKeyNames() {
    return new String[]{ "id" };
  }

  @Override
  public String[] getColumnNames() {
    return new String[]{ "id","name","above_id","is_deprecated","version","realm_id","create_user_id","create_time","update_user_id","update_time","create_group_id","__typename" };
  }

  public Integer getId() {
    return this.id;
  }

  public Organization setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public Organization setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getAboveId() {
    return this.aboveId;
  }

  public Organization setAboveId(Integer aboveId) {
    this.aboveId = aboveId;
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

  public String getRealmId() {
    return this.realmId;
  }

  public Organization setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Organization setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Organization setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Organization setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Organization setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Organization setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Organization setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
