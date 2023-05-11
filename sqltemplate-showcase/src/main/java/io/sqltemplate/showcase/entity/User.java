package io.sqltemplate.showcase.entity;

import io.sqltemplate.active.record.model.Record;
import io.sqltemplate.showcase.templates.Sex;

import java.time.LocalDateTime;

public class User extends Record<User> {

    private String id;

    private String name;

    private int age;

    private Sex sex;

    public LocalDateTime birthday;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public static User User() {
        return new User();
    }
}
