package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;

public class Directive extends Record<Directive> {
  private String name;

  private Integer schemaId;

  private String description;

  private Boolean isRepeatable;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  public String getName() {
    return this.name;
  }

  public Directive setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getSchemaId() {
    return this.schemaId;
  }

  public Directive setSchemaId(Integer schemaId) {
    this.schemaId = schemaId;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public Directive setDescription(String description) {
    this.description = description;
    return this;
  }

  public Boolean getIsRepeatable() {
    return this.isRepeatable;
  }

  public Directive setIsRepeatable(Boolean isRepeatable) {
    this.isRepeatable = isRepeatable;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Directive setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Directive setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public Directive setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Directive setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Directive setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Directive setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Directive setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Directive setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Directive setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
