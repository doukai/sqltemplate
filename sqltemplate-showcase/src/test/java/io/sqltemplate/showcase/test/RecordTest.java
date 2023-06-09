package io.sqltemplate.showcase.test;

import io.sqltemplate.showcase.dto.Organization;
import io.sqltemplate.showcase.dto.Role;
import io.sqltemplate.showcase.dto.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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
        User user = new User()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .insert();

        assertAll(
                () -> assertEquals(user.getName(), "Robin Castillo"),
                () -> assertEquals(user.getLogin(), "castillo"),
                () -> assertEquals(user.getPassword(), "96954"),
                () -> assertEquals(user.getAge(), 9)
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
        User user = new User()
                .setId((int) users.get(0).get("id"))
                .setName("Robert Castillo")
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .update();

        assertAll(
                () -> assertEquals(user.getName(), "Robert Castillo"),
                () -> assertEquals(user.getLogin(), "castillo"),
                () -> assertEquals(user.getPassword(), "96954"),
                () -> assertEquals(user.getAge(), 9)
        );
    }

    @Test
    void updateAll() {
        tableInsert();
        List<User> userList = User.updateAll(
                new User()
                        .setId((int) users.get(0).get("id"))
                        .setName("Robert Castillo")
                        .setLogin((String) users.get(0).get("login"))
                        .setPassword((String) users.get(0).get("password"))
                        .setAge((int) users.get(0).get("age")),
                new User()
                        .setId((int) users.get(2).get("id"))
                        .setName("Mary England")
                        .setLogin((String) users.get(2).get("login"))
                        .setPassword((String) users.get(2).get("password"))
                        .setAge((int) users.get(2).get("age"))
        );

        assertAll(
                () -> assertEquals(userList.get(0).getName(), "Robert Castillo"),
                () -> assertEquals(userList.get(1).getName(), "Mary England")
        );
    }

    @Test
    void delete() {
        tableInsert();
        boolean delete = new User()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .delete();

        assertTrue(delete);
    }

    @Test
    void deleteAll() {
        tableInsert();
        long count = User.deleteAll(
                new User()
                        .setId((int) users.get(0).get("id"))
                        .setName((String) users.get(0).get("name"))
                        .setLogin((String) users.get(0).get("login"))
                        .setPassword((String) users.get(0).get("password"))
                        .setAge((int) users.get(0).get("age")),
                new User()
                        .setId((int) users.get(2).get("id"))
                        .setName((String) users.get(2).get("name"))
                        .setLogin((String) users.get(2).get("login"))
                        .setPassword((String) users.get(2).get("password"))
                        .setAge((int) users.get(2).get("age"))
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
    void addOne() {
        organizationTableInsert();
        User user = new User()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .insert();

        Organization organization = Organization.get(1);
        organization.addUser(user);
        List<User> userList = organization.getUserList();

        assertAll(
                () -> assertEquals(userList.get(0).getId(), 1),
                () -> assertEquals(userList.get(0).getName(), "Robin Castillo")
        );
    }

    @Test
    void addMany() {
        organizationTableInsert();
        User user1 = new User()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .insert();
        User user2 = new User()
                .setId((int) users.get(1).get("id"))
                .setName((String) users.get(1).get("name"))
                .setLogin((String) users.get(1).get("login"))
                .setPassword((String) users.get(1).get("password"))
                .setAge((int) users.get(1).get("age"))
                .insert();

        Organization organization = Organization.get(1);
        organization.addUserList(Arrays.asList(user1, user2));
        List<User> userList = organization.getUserList();

        assertAll(
                () -> assertEquals(userList.size(), 2),
                () -> assertEquals(userList.get(0).getName(), "Robin Castillo"),
                () -> assertEquals(userList.get(1).getName(), "Kelly Villarreal")
        );
    }

    @Test
    void addManyByJoin() {
        userTableInsert();
        Role role1 = new Role()
                .setId((int) roles.get(0).get("id"))
                .setName((String) roles.get(0).get("name"))
                .insert();
        Role role2 = new Role()
                .setId((int) roles.get(1).get("id"))
                .setName((String) roles.get(1).get("name"))
                .insert();

        User user = User.get(1);
        user.addRoleList(Arrays.asList(role1, role2));
        List<Role> roleList = user.getRoleList();

        assertAll(
                () -> assertEquals(roleList.size(), 2),
                () -> assertEquals(roleList.get(0).getName(), "User"),
                () -> assertEquals(roleList.get(1).getName(), "Admin")
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

    @Test
    void removeManyByJoin() {
        tableInsert();
        User user1 = User.get(1);
        long deleteCount = user1.removeRoleList(Arrays.asList(Role.get(1), Role.get(2)));
        List<Role> roleList = user1.getRoleList();

        assertAll(
                () -> assertEquals(deleteCount, 2),
                () -> assertEquals(roleList.size(), 0)
        );
    }
}
