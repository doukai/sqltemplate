package io.sqltemplate.showcase.entity;

import io.sqltemplate.active.record.model.Record;
import io.sqltemplate.active.record.model.expression.Expression;
import io.sqltemplate.showcase.templates.Sex;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRecord extends Record<UserRecord> {

    private String id;

    private String name;

    private String login;

    private String password;

    private int age;

    private Sex sex;

    public LocalDateTime birthday;

    public String getId() {
        return id;
    }

    public UserRecord setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserRecord setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public UserRecord setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRecord setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserRecord setAge(int age) {
        this.age = age;
        return this;
    }

    public Sex getSex() {
        return sex;
    }

    public UserRecord setSex(Sex sex) {
        this.sex = sex;
        return this;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public UserRecord setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
        return this;
    }

    @Override
    protected String getTableName() {
        return "user";
    }

    @Override
    protected String getKeyName() {
        return "id";
    }

    @Override
    protected Object getKeyValue() {
        return getId();
    }

    @Override
    protected List<String> getColumnNames() {
        return Arrays.asList("id", "name", "login", "password", "age", "sex");
    }

    @Override
    protected List<Expression> getValues() {
        return Arrays.asList(Expression.of(getId()), Expression.of(getName()), Expression.of(getLogin()), Expression.of(getPassword()), Expression.of(getAge()), Expression.of(getSex()));
    }

    @Override
    protected Map<String, Expression> entityToMap() {
        return new HashMap<>() {{
            put("id", Expression.of(getId()));
            put("name", Expression.of(getName()));
            put("login", Expression.of(getLogin()));
            put("password", Expression.of(getPassword()));
            put("age", Expression.of(getAge()));
            put("sex", Expression.of(getSex()));
        }};
    }

    @Override
    protected UserRecord mapToEntity(Map<String, Object> result) {
        UserRecord userRecord = new UserRecord();
        userRecord.setId((String) result.get("id"));
        userRecord.setName((String) result.get("name"));
        userRecord.setAge((int) result.get("age"));
        userRecord.setSex((Sex) result.get("sex"));
        return userRecord;
    }
}
