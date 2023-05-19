package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserMobileNumbers extends Record<UserMobileNumbers> {
  private Integer id;

  private Integer userId;

  private String mobileNumber;

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
    return "user_mobile_numbers";
  }

  @Override
  public List<String> getKeyNames() {
    return new ArrayList<String>() {{ add("id"); }};
  }

  public Integer getId() {
    return this.id;
  }

  public UserMobileNumbers setId(Integer id) {
    this.id = id;
    return this;
  }

  public Integer getUserId() {
    return this.userId;
  }

  public UserMobileNumbers setUserId(Integer userId) {
    this.userId = userId;
    return this;
  }

  public String getMobileNumber() {
    return this.mobileNumber;
  }

  public UserMobileNumbers setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public UserMobileNumbers setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public UserMobileNumbers setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public UserMobileNumbers setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public UserMobileNumbers setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public UserMobileNumbers setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public UserMobileNumbers setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public UserMobileNumbers setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public UserMobileNumbers setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public UserMobileNumbers setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
