package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;

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
