package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

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

  private final String tableName = "user_mobile_numbers";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "user_id", "mobile_number", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

  @Override
  public String getTableName() {
    return tableName;
  }

  @Override
  public String[] getKeyNames() {
    return keyNames;
  }

  @Override
  public String[] getColumnNames() {
    return columnNames;
  }

  @Override
  public Object getValue(String columnName) {
    if (columnName.equals("id")) {
      return getId();
    } else if (columnName.equals("user_id")) {
      return getUserId();
    } else if (columnName.equals("mobile_number")) {
      return getMobileNumber();
    } else if (columnName.equals("is_deprecated")) {
      return getIsDeprecated();
    } else if (columnName.equals("version")) {
      return getVersion();
    } else if (columnName.equals("realm_id")) {
      return getRealmId();
    } else if (columnName.equals("create_user_id")) {
      return getCreateUserId();
    } else if (columnName.equals("create_time")) {
      return getCreateTime();
    } else if (columnName.equals("update_user_id")) {
      return getUpdateUserId();
    } else if (columnName.equals("update_time")) {
      return getUpdateTime();
    } else if (columnName.equals("create_group_id")) {
      return getCreateGroupId();
    } else if (columnName.equals("__typename")) {
      return getTypename();
    }
    return null;
  }

  @Override
  public UserMobileNumbers mapToEntity(Map<String, Object> result) {
    UserMobileNumbers entity = new UserMobileNumbers();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setUserId(result.get("userId") != null ? (Integer) result.get("userId") : null);
    entity.setMobileNumber(result.get("mobileNumber") != null ? (String) result.get("mobileNumber") : null);
    entity.setIsDeprecated(result.get("isDeprecated") != null ? (Boolean) result.get("isDeprecated") : null);
    entity.setVersion(result.get("version") != null ? (Integer) result.get("version") : null);
    entity.setRealmId(result.get("realmId") != null ? (String) result.get("realmId") : null);
    entity.setCreateUserId(result.get("createUserId") != null ? (String) result.get("createUserId") : null);
    entity.setCreateTime(result.get("createTime") != null ? (LocalDateTime) result.get("createTime") : null);
    entity.setUpdateUserId(result.get("updateUserId") != null ? (String) result.get("updateUserId") : null);
    entity.setUpdateTime(result.get("updateTime") != null ? (LocalDateTime) result.get("updateTime") : null);
    entity.setCreateGroupId(result.get("createGroupId") != null ? (String) result.get("createGroupId") : null);
    entity.setTypename(result.get("Typename") != null ? (String) result.get("Typename") : null);
    return entity;
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
