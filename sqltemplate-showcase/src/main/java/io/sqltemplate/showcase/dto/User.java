package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Map;

public class User extends Record<User> {
  private Integer id;

  private String login;

  private String password;

  private String name;

  private Integer age;

  private Boolean disabled;

  private String sex;

  private Integer organizationId;

  private Boolean isDeprecated;

  private Integer version;

  private String realmId;

  private String createUserId;

  private LocalDateTime createTime;

  private String updateUserId;

  private LocalDateTime updateTime;

  private String createGroupId;

  private String Typename;

  private final String tableName = "user";

  private final String[] keyNames = new String[]{"id"};

  private final String[] columnNames = new String[]{"id", "login", "password", "name", "age", "disabled", "sex", "organization_id", "is_deprecated", "version", "realm_id", "create_user_id", "create_time", "update_user_id", "update_time", "create_group_id", "__typename"};

  private final Boolean autoIncrement = true;

  @Override
  protected String getTableName() {
    return tableName;
  }

  @Override
  protected String[] getKeyNames() {
    return keyNames;
  }

  @Override
  protected String[] getColumnNames() {
    return columnNames;
  }

  @Override
  protected Object getValue(String columnName) {
    if (columnName.equals("id")) {
      return getId();
    } else if (columnName.equals("login")) {
      return getLogin();
    } else if (columnName.equals("password")) {
      return getPassword();
    } else if (columnName.equals("name")) {
      return getName();
    } else if (columnName.equals("age")) {
      return getAge();
    } else if (columnName.equals("disabled")) {
      return getDisabled();
    } else if (columnName.equals("sex")) {
      return getSex();
    } else if (columnName.equals("organization_id")) {
      return getOrganizationId();
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
  protected User mapToEntity(Map<String, Object> result) {
    User entity = new User();
    entity.setId(result.get("id") != null ? (Integer) result.get("id") : null);
    entity.setLogin(result.get("login") != null ? (String) result.get("login") : null);
    entity.setPassword(result.get("password") != null ? (String) result.get("password") : null);
    entity.setName(result.get("name") != null ? (String) result.get("name") : null);
    entity.setAge(result.get("age") != null ? (Integer) result.get("age") : null);
    entity.setDisabled(result.get("disabled") != null ? (Boolean) result.get("disabled") : null);
    entity.setSex(result.get("sex") != null ? (String) result.get("sex") : null);
    entity.setOrganizationId(result.get("organizationId") != null ? (Integer) result.get("organizationId") : null);
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
  protected Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public Integer getId() {
    return this.id;
  }

  public User setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getLogin() {
    return this.login;
  }

  public User setLogin(String login) {
    this.login = login;
    return this;
  }

  public String getPassword() {
    return this.password;
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public User setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getAge() {
    return this.age;
  }

  public User setAge(Integer age) {
    this.age = age;
    return this;
  }

  public Boolean getDisabled() {
    return this.disabled;
  }

  public User setDisabled(Boolean disabled) {
    this.disabled = disabled;
    return this;
  }

  public String getSex() {
    return this.sex;
  }

  public User setSex(String sex) {
    this.sex = sex;
    return this;
  }

  public Integer getOrganizationId() {
    return this.organizationId;
  }

  public User setOrganizationId(Integer organizationId) {
    this.organizationId = organizationId;
    return this;
  }

  public Boolean getIsDeprecated() {
    return this.isDeprecated;
  }

  public User setIsDeprecated(Boolean isDeprecated) {
    this.isDeprecated = isDeprecated;
    return this;
  }

  public Integer getVersion() {
    return this.version;
  }

  public User setVersion(Integer version) {
    this.version = version;
    return this;
  }

  public String getRealmId() {
    return this.realmId;
  }

  public User setRealmId(String realmId) {
    this.realmId = realmId;
    return this;
  }

  public String getCreateUserId() {
    return this.createUserId;
  }

  public User setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public User setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public String getUpdateUserId() {
    return this.updateUserId;
  }

  public User setUpdateUserId(String updateUserId) {
    this.updateUserId = updateUserId;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public User setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String getCreateGroupId() {
    return this.createGroupId;
  }

  public User setCreateGroupId(String createGroupId) {
    this.createGroupId = createGroupId;
    return this;
  }

  public String getTypename() {
    return this.Typename;
  }

  public User setTypename(String Typename) {
    this.Typename = Typename;
    return this;
  }
}
