package io.sqltemplate.showcase.dto;

import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDateTime;

public class Schema {
  private Integer id;

  private String queryTypeName;

  private String mutationTypeName;

  private String subscriptionTypeName;

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

  public void setId(Integer id) {
    this.id = id;
  }

  public String getQueryTypeName() {
    return this.queryTypeName;
  }

  public void setQueryTypeName(String queryTypeName) {
    this.queryTypeName = queryTypeName;
  }

  public String getMutationTypeName() {
    return this.mutationTypeName;
  }

  public void setMutationTypeName(String mutationTypeName) {
    this.mutationTypeName = mutationTypeName;
  }

  public String getSubscriptionTypeName() {
    return this.subscriptionTypeName;
  }

  public void setSubscriptionTypeName(String subscriptionTypeName) {
    this.subscriptionTypeName = subscriptionTypeName;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public void setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
  }

  public Integer getVersion() {
    return this.version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public void setRealmId(String realmId) {
    this.realmId = realmId;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public void setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public void setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public void setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
  }

  public String getTypename() {
    return this.Typename;
  }

  public void setTypename(String Typename) {
    this.Typename = Typename;
  }
}
