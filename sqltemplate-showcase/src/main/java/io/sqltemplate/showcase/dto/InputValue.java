package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;

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
