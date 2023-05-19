package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;

public class TypePossibleTypes extends Record<TypePossibleTypes> {
  private Integer id;

  private String typeName;

  private String possibleTypeName;

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
    return "__type_possible_types";
  }

  @Override
  public String[] getKeyNames() {
    return new String[]{ "id" };
  }

  @Override
  public String[] getColumnNames() {
    return new String[]{ "id","type_name","possible_type_name","is_deprecated","version","realm_id","create_user_id","create_time","update_user_id","update_time","create_group_id","__typename" };
  }

  public Integer getId() {
    return this.id;
  }

  public TypePossibleTypes setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getTypeName() {
    return this.typeName;
  }

  public TypePossibleTypes setTypeName(String typeName) {
    this.typeName = typeName;
    return this;
  }

  public String getPossibleTypeName() {
    return this.possibleTypeName;
  }

  public TypePossibleTypes setPossibleTypeName(String possibleTypeName) {
    this.possibleTypeName = possibleTypeName;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public TypePossibleTypes setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public TypePossibleTypes setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public TypePossibleTypes setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public TypePossibleTypes setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public TypePossibleTypes setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public TypePossibleTypes setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public TypePossibleTypes setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public TypePossibleTypes setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public TypePossibleTypes setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
