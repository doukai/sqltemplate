package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Field extends Record<Field> {
  private Integer id;

  private String name;

  private String typeName;

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

  @Override
  public String getTableName() {
    return "__field";
  }

  @Override
  public List<String> getKeyNames() {
    return new ArrayList<String>() {{ add("id"); }};
  }

  public Integer getId() {
    return this.id;
  }

  public Field setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public Field setName(String name) {
    this.name = name;
    return this;
  }

  public String getTypeName() {
    return this.typeName;
  }

  public Field setTypeName(String typeName) {
    this.typeName = typeName;
    return this;
  }

  public String getOfTypeName() {
    return this.ofTypeName;
  }

  public Field setOfTypeName(String ofTypeName) {
    this.ofTypeName = ofTypeName;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public Field setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getDeprecationReason() {
    return this.deprecationReason;
  }

  public Field setDeprecationReason(String deprecationReason) {
    this.deprecationReason = deprecationReason;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Field setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Field setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public Field setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Field setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Field setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Field setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Field setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Field setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Field setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
