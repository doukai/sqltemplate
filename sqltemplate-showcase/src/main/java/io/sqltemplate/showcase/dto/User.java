package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  @Override
  public String getTableName() {
    return "user";
  }

  @Override
  public List<String> getKeyNames() {
    return new ArrayList<String>() {{ add("id"); }};
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
