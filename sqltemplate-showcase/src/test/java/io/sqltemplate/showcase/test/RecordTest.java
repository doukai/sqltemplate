package io.sqltemplate.showcase.test;

import io.sqltemplate.showcase.dto.Organization;
import io.sqltemplate.showcase.dto.Role;
import io.sqltemplate.showcase.dto.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.sqltemplate.active.record.model.conditional.LK.lk;
import static io.sqltemplate.showcase.test.Setup.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecordTest {

    @BeforeAll
    static void beforeAll() {
        tableInit();
    }

    @BeforeEach
    void beforeEach() {
        tableClear();
    }

    @Test
    void insert() {
        Map<String, Object> user = users.get(0);
        User insertedUser = new User()
                .setId((int) user.get("id"))
                .setName((String) user.get("name"))
                .setLogin((String) user.get("login"))
                .setPassword((String) user.get("password"))
                .setAge((int) user.get("age"))
                .insert();

        assertAll(
                () -> assertEquals(insertedUser.getName(), "Robin Castillo"),
                () -> assertEquals(insertedUser.getLogin(), "castillo"),
                () -> assertEquals(insertedUser.getPassword(), "96954"),
                () -> assertEquals(insertedUser.getAge(), 9)
        );
    }

    @Test
    void insertAll() {
        User.insertAll(
                users.stream()
                        .map(user ->
                                new User()
                                        .setId((int) user.get("id"))
                                        .setName((String) user.get("name"))
                                        .setLogin((String) user.get("login"))
                                        .setPassword((String) user.get("password"))
                                        .setAge((int) user.get("age"))
                        )
                        .toArray(User[]::new)
        );

        assertEquals(User.all().size(), 4);
    }

    @Test
    void get() {
        tableInsert();
        User user = User.get(1);
        assertAll(
                () -> assertEquals(user.getName(), "Robin Castillo"),
                () -> assertEquals(user.getLogin(), "castillo"),
                () -> assertEquals(user.getPassword(), "96954"),
                () -> assertEquals(user.getAge(), 9)
        );
    }

    @Test
    void select() {
        tableInsert();
        List<User> userList = User.where(lk("name", "%la%")).list();
        assertAll(
                () -> assertEquals(userList.get(0).getName(), "Kelly Villarreal"),
                () -> assertEquals(userList.get(0).getLogin(), "villarreal"),
                () -> assertEquals(userList.get(0).getPassword(), "54368"),
                () -> assertEquals(userList.get(0).getAge(), 18),
                () -> assertEquals(userList.get(1).getName(), "Malia England"),
                () -> assertEquals(userList.get(1).getLogin(), "england"),
                () -> assertEquals(userList.get(1).getPassword(), "68925"),
                () -> assertEquals(userList.get(1).getAge(), 27)
        );
    }

    @Test
    void count() {
        tableInsert();
        long allCount = User.allCount();
        long count = User.where(lk("name", "%la%")).count();
        assertAll(
                () -> assertEquals(allCount, 4),
                () -> assertEquals(count, 2)
        );
    }

    @Test
    void exists() {
        tableInsert();
        boolean exists = User.where(lk("name", "%la%")).exists();
        assertTrue(exists);
    }

    @Test
    void sort() {
        tableInsert();
        List<User> userList = User.record().desc("age").list();
        assertAll(
                () -> assertEquals(userList.get(0).getId(), 4),
                () -> assertEquals(userList.get(1).getId(), 3),
                () -> assertEquals(userList.get(2).getId(), 2),
                () -> assertEquals(userList.get(3).getId(), 1)
        );
    }

    @Test
    void update() {
        tableInsert();
        Map<String, Object> user = users.get(0);
        User insertedUser = new User()
                .setId((int) user.get("id"))
                .setName("Robert Castillo")
                .setLogin((String) user.get("login"))
                .setPassword((String) user.get("password"))
                .setAge((int) user.get("age"))
                .update();

        assertAll(
                () -> assertEquals(insertedUser.getName(), "Robert Castillo"),
                () -> assertEquals(insertedUser.getLogin(), "castillo"),
                () -> assertEquals(insertedUser.getPassword(), "96954"),
                () -> assertEquals(insertedUser.getAge(), 9)
        );
    }

    @Test
    void updateAll() {
        tableInsert();
        Map<String, Object> user1 = users.get(0);
        Map<String, Object> user2 = users.get(2);

        List<User> userList = User.updateAll(
                new User()
                        .setId((int) user1.get("id"))
                        .setName("Robert Castillo")
                        .setLogin((String) user1.get("login"))
                        .setPassword((String) user1.get("password"))
                        .setAge((int) user1.get("age")),
                new User()
                        .setId((int) user2.get("id"))
                        .setName("Mary England")
                        .setLogin((String) user2.get("login"))
                        .setPassword((String) user2.get("password"))
                        .setAge((int) user2.get("age"))
        );

        assertAll(
                () -> assertEquals(userList.get(0).getName(), "Robert Castillo"),
                () -> assertEquals(userList.get(1).getName(), "Mary England")
        );
    }

    @Test
    void delete() {
        tableInsert();
        Map<String, Object> user = users.get(0);
        boolean delete = new User()
                .setId((int) user.get("id"))
                .setName((String) user.get("name"))
                .setLogin((String) user.get("login"))
                .setPassword((String) user.get("password"))
                .setAge((int) user.get("age"))
                .delete();

        assertTrue(delete);
    }

    @Test
    void deleteAll() {
        tableInsert();
        Map<String, Object> user1 = users.get(0);
        Map<String, Object> user2 = users.get(2);

        long count = User.deleteAll(
                new User()
                        .setId((int) user1.get("id"))
                        .setName((String) user1.get("name"))
                        .setLogin((String) user1.get("login"))
                        .setPassword((String) user1.get("password"))
                        .setAge((int) user1.get("age")),
                new User()
                        .setId((int) user2.get("id"))
                        .setName((String) user2.get("name"))
                        .setLogin((String) user2.get("login"))
                        .setPassword((String) user2.get("password"))
                        .setAge((int) user2.get("age"))
        );

        assertEquals(count, 2);
    }

    @Test
    void getManyToOne() {
        tableInsert();
        User user1 = User.get(2);
        User user2 = User.get(3);

        assertAll(
                () -> assertEquals(user1.getOrganization().getName(), "Google"),
                () -> assertEquals(user2.getOrganization().getName(), "Microsoft")
        );
    }

    @Test
    void getOneToMany() {
        tableInsert();
        Organization organization3 = Organization.get(3);
        List<User> userList = organization3.getUserList();

        assertAll(
                () -> assertEquals(userList.get(0).getName(), "Robin Castillo"),
                () -> assertEquals(userList.get(1).getName(), "Kelly Villarreal")
        );
    }

    @Test
    void getManyToMany() {
        tableInsert();
        List<Role> roleList = User.get(1).getRoleList();

        assertAll(
                () -> assertEquals(roleList.get(0).getName(), "User"),
                () -> assertEquals(roleList.get(1).getName(), "Admin")
        );
    }

    @Test
    void AddOne() {
        organizationTableInsert();
        Map<String, Object> user = users.get(0);
        User insertedUser = new User()
                .setId((int) user.get("id"))
                .setName((String) user.get("name"))
                .setLogin((String) user.get("login"))
                .setPassword((String) user.get("password"))
                .setAge((int) user.get("age"))
                .insert();

        Organization organization = Organization.get(1);
        organization.addUser(insertedUser);
        List<User> userList = organization.getUserList();

        assertAll(
                () -> assertEquals(userList.get(0).getId(), 1),
                () -> assertEquals(userList.get(0).getName(), "Robin Castillo")
        );
    }

    @Test
    void AddMany() {
        organizationTableInsert();
        Map<String, Object> user1 = users.get(0);
        Map<String, Object> user2 = users.get(1);
        User insertedUser1 = new User()
                .setId((int) user1.get("id"))
                .setName((String) user1.get("name"))
                .setLogin((String) user1.get("login"))
                .setPassword((String) user1.get("password"))
                .setAge((int) user1.get("age"))
                .insert();
        User insertedUser2 = new User()
                .setId((int) user2.get("id"))
                .setName((String) user2.get("name"))
                .setLogin((String) user2.get("login"))
                .setPassword((String) user2.get("password"))
                .setAge((int) user2.get("age"))
                .insert();

        Organization organization = Organization.get(1);
        organization.addUserList(Arrays.asList(insertedUser1, insertedUser2));
        List<User> userList = organization.getUserList();

        assertAll(
                () -> assertEquals(userList.size(), 2),
                () -> assertEquals(userList.get(0).getName(), "Robin Castillo"),
                () -> assertEquals(userList.get(1).getName(), "Kelly Villarreal")
        );
    }

    @Test
    void removeOne() {
        tableInsert();
        Organization organization = Organization.get(3);
        organization.removeUser(User.get(1));
        List<User> userList = organization.getUserList();

        assertAll(
                () -> assertEquals(userList.size(), 1),
                () -> assertEquals(userList.get(0).getId(), 2),
                () -> assertEquals(userList.get(0).getName(), "Kelly Villarreal")
        );
    }

    @Test
    void removeMany() {
        tableInsert();
        Organization organization = Organization.get(3);
        organization.removeUserList(Arrays.asList(User.get(1), User.get(2)));
        List<User> userList = organization.getUserList();

        assertAll(
                () -> assertEquals(userList.size(), 0)
        );
    }
}
