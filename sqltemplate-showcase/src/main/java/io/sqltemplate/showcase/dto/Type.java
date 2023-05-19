package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Type extends Record<Type> {
  private String name;

  private Integer schemaId;

  private String kind;

  private String description;

  private String ofTypeName;

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
    return "__type";
  }

  @Override
  public List<String> getKeyNames() {
    return new ArrayList<String>() {{ add("name"); }};
  }

  public String getName() {
    return this.name;
  }

  public Type setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getSchemaId() {
    return this.schemaId;
  }

  public Type setSchemaId(Integer schemaId) {
    this.schemaId = schemaId;
    return this;
  }

  public String getKind() {
    return this.kind;
  }

  public Type setKind(String kind) {
    this.kind = kind;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public Type setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getOfTypeName() {
    return this.ofTypeName;
  }

  public Type setOfTypeName(String ofTypeName) {
    this.ofTypeName = ofTypeName;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Type setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Type setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public Type setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Type setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Type setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Type setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Type setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Type setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Type setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
