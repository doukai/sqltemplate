package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;

public class DirectiveLocations extends Record<DirectiveLocations> {
  private Integer id;

  private String directiveName;

  private String directiveLocation;

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

  public DirectiveLocations setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getDirectiveName() {
    return this.directiveName;
  }

  public DirectiveLocations setDirectiveName(String directiveName) {
    this.directiveName = directiveName;
    return this;
  }

  public String getDirectiveLocation() {
    return this.directiveLocation;
  }

  public DirectiveLocations setDirectiveLocation(String directiveLocation) {
    this.directiveLocation = directiveLocation;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public DirectiveLocations setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public DirectiveLocations setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public DirectiveLocations setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public DirectiveLocations setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public DirectiveLocations setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public DirectiveLocations setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public DirectiveLocations setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public DirectiveLocations setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public DirectiveLocations setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
