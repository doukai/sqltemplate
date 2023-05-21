package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class Schema extends Record<Schema> {
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

  private final String tableName = "__schema";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "query_type_name", "mutation_type_name", "subscription_type_name", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

  private final Boolean autoIncrement = true;

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
    } else if (columnName.equals("query_type_name")) {
      return getQueryTypeName();
    } else if (columnName.equals("mutation_type_name")) {
      return getMutationTypeName();
    } else if (columnName.equals("subscription_type_name")) {
      return getSubscriptionTypeName();
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
  public Schema mapToEntity(Map<String, Object> result) {
    Schema entity = new Schema();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setQueryTypeName(result.get("queryTypeName") != null ? (String) result.get("queryTypeName") : null);
    entity.setMutationTypeName(result.get("mutationTypeName") != null ? (String) result.get("mutationTypeName") : null);
    entity.setSubscriptionTypeName(result.get("subscriptionTypeName") != null ? (String) result.get("subscriptionTypeName") : null);
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

  @Override
  public Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public Integer getId() {
    return this.id;
  }

  public Schema setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getQueryTypeName() {
    return this.queryTypeName;
  }

  public Schema setQueryTypeName(String queryTypeName) {
    this.queryTypeName = queryTypeName;
    return this;
  }

  public String getMutationTypeName() {
    return this.mutationTypeName;
  }

  public Schema setMutationTypeName(String mutationTypeName) {
    this.mutationTypeName = mutationTypeName;
    return this;
  }

  public String getSubscriptionTypeName() {
    return this.subscriptionTypeName;
  }

  public Schema setSubscriptionTypeName(String subscriptionTypeName) {
    this.subscriptionTypeName = subscriptionTypeName;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public Schema setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Schema setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public Schema setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public Schema setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public Schema setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public Schema setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public Schema setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public Schema setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public Schema setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
