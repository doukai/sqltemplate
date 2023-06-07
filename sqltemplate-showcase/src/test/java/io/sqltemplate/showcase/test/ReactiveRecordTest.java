package io.sqltemplate.showcase.test;

import io.sqltemplate.showcase.dto.ReactiveOrganization;
import io.sqltemplate.showcase.dto.ReactiveRole;
import io.sqltemplate.showcase.dto.ReactiveUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static io.sqltemplate.active.record.model.conditional.LK.lk;
import static io.sqltemplate.showcase.test.Setup.organizationTableInsert;
import static io.sqltemplate.showcase.test.Setup.roles;
import static io.sqltemplate.showcase.test.Setup.tableClear;
import static io.sqltemplate.showcase.test.Setup.tableInit;
import static io.sqltemplate.showcase.test.Setup.tableInsert;
import static io.sqltemplate.showcase.test.Setup.userTableInsert;
import static io.sqltemplate.showcase.test.Setup.users;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReactiveRecordTest {

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
        Mono<ReactiveUser> userMono = new ReactiveUser()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .insert();

        StepVerifier.create(userMono)
                .assertNext(user ->
                        assertAll(
                                () -> assertEquals(user.getName(), "Robin Castillo"),
                                () -> assertEquals(user.getLogin(), "castillo"),
                                () -> assertEquals(user.getPassword(), "96954"),
                                () -> assertEquals(user.getAge(), 9)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void insertAll() {
        Mono<List<ReactiveUser>> userListMono = ReactiveUser.insertAll(
                users.stream()
                        .map(user ->
                                new ReactiveUser()
                                        .setId((int) user.get("id"))
                                        .setName((String) user.get("name"))
                                        .setLogin((String) user.get("login"))
                                        .setPassword((String) user.get("password"))
                                        .setAge((int) user.get("age"))
                        )
                        .toArray(ReactiveUser[]::new)
        );

        StepVerifier.create(userListMono.then(ReactiveUser.all()))
                .assertNext(userList -> assertEquals(userList.size(), 4))
                .expectComplete()
                .verify();
    }

    @Test
    void get() {
        tableInsert();
        Mono<ReactiveUser> userMono = ReactiveUser.get(1);

        StepVerifier.create(userMono)
                .assertNext(user ->
                        assertAll(
                                () -> assertEquals(user.getName(), "Robin Castillo"),
                                () -> assertEquals(user.getLogin(), "castillo"),
                                () -> assertEquals(user.getPassword(), "96954"),
                                () -> assertEquals(user.getAge(), 9)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void select() {
        tableInsert();
        Mono<List<ReactiveUser>> userListMono = ReactiveUser.where(lk("name", "%la%")).list();

        StepVerifier.create(userListMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.get(0).getName(), "Kelly Villarreal"),
                                () -> assertEquals(userList.get(0).getLogin(), "villarreal"),
                                () -> assertEquals(userList.get(0).getPassword(), "54368"),
                                () -> assertEquals(userList.get(0).getAge(), 18),
                                () -> assertEquals(userList.get(1).getName(), "Malia England"),
                                () -> assertEquals(userList.get(1).getLogin(), "england"),
                                () -> assertEquals(userList.get(1).getPassword(), "68925"),
                                () -> assertEquals(userList.get(1).getAge(), 27)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void count() {
        tableInsert();
        Mono<Long> allCountMono = ReactiveUser.allCount();
        Mono<Long> countMono = ReactiveUser.where(lk("name", "%la%")).count();

        StepVerifier.create(Flux.concat(allCountMono, countMono))
                .expectNext(4L)
                .expectNext(2L)
                .expectComplete()
                .verify();
    }

    @Test
    void exists() {
        tableInsert();
        Mono<Boolean> existsMono = ReactiveUser.where(lk("name", "%la%")).exists();

        StepVerifier.create(existsMono)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    void sort() {
        tableInsert();
        Mono<List<ReactiveUser>> ageMono = ReactiveUser.record().desc("age").list();

        StepVerifier.create(ageMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.get(0).getId(), 4),
                                () -> assertEquals(userList.get(1).getId(), 3),
                                () -> assertEquals(userList.get(2).getId(), 2),
                                () -> assertEquals(userList.get(3).getId(), 1)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        tableInsert();
        Mono<ReactiveUser> userMono = new ReactiveUser()
                .setId((int) users.get(0).get("id"))
                .setName("Robert Castillo")
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .update();

        StepVerifier.create(userMono)
                .assertNext(user ->
                        assertAll(
                                () -> assertEquals(user.getName(), "Robert Castillo"),
                                () -> assertEquals(user.getLogin(), "castillo"),
                                () -> assertEquals(user.getPassword(), "96954"),
                                () -> assertEquals(user.getAge(), 9)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void updateAll() {
        tableInsert();
        Mono<List<ReactiveUser>> userListMono = ReactiveUser.updateAll(
                new ReactiveUser()
                        .setId((int) users.get(0).get("id"))
                        .setName("Robert Castillo")
                        .setLogin((String) users.get(0).get("login"))
                        .setPassword((String) users.get(0).get("password"))
                        .setAge((int) users.get(0).get("age")),
                new ReactiveUser()
                        .setId((int) users.get(2).get("id"))
                        .setName("Mary England")
                        .setLogin((String) users.get(2).get("login"))
                        .setPassword((String) users.get(2).get("password"))
                        .setAge((int) users.get(2).get("age"))
        );

        StepVerifier.create(userListMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.get(0).getName(), "Robert Castillo"),
                                () -> assertEquals(userList.get(1).getName(), "Mary England")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void delete() {
        tableInsert();
        Mono<Boolean> deleteMono = new ReactiveUser()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"))
                .delete();

        StepVerifier.create(deleteMono)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteAll() {
        tableInsert();
        Mono<Long> countMono = ReactiveUser.deleteAll(
                new ReactiveUser()
                        .setId((int) users.get(0).get("id"))
                        .setName((String) users.get(0).get("name"))
                        .setLogin((String) users.get(0).get("login"))
                        .setPassword((String) users.get(0).get("password"))
                        .setAge((int) users.get(0).get("age")),
                new ReactiveUser()
                        .setId((int) users.get(2).get("id"))
                        .setName((String) users.get(2).get("name"))
                        .setLogin((String) users.get(2).get("login"))
                        .setPassword((String) users.get(2).get("password"))
                        .setAge((int) users.get(2).get("age"))
        );

        StepVerifier.create(countMono)
                .expectNext(2L)
                .expectComplete()
                .verify();
    }

    @Test
    void getManyToOne() {
        tableInsert();
        Flux<ReactiveOrganization> organizationFlux = Flux.concat(ReactiveUser.get(2), ReactiveUser.get(3)).flatMap(ReactiveUser::getOrganization);

        StepVerifier.create(organizationFlux)
                .assertNext(organization -> assertEquals(organization.getName(), "Google"))
                .assertNext(organization -> assertEquals(organization.getName(), "Microsoft"))
                .expectComplete()
                .verify();
    }

    @Test
    void getOneToMany() {
        tableInsert();
        Mono<List<ReactiveUser>> userListMono = ReactiveOrganization.get(3).flatMap(ReactiveOrganization::getUserList);

        StepVerifier.create(userListMono)
                .assertNext(userList -> assertAll(
                                () -> assertEquals(userList.get(0).getName(), "Robin Castillo"),
                                () -> assertEquals(userList.get(1).getName(), "Kelly Villarreal")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void getManyToMany() {
        tableInsert();
        Mono<List<ReactiveRole>> roleListMono = ReactiveUser.get(1).flatMap(ReactiveUser::getRoleList);

        StepVerifier.create(roleListMono)
                .assertNext(roleList -> assertAll(
                                () -> assertEquals(roleList.get(0).getName(), "User"),
                                () -> assertEquals(roleList.get(1).getName(), "Admin")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void addOne() {
        organizationTableInsert();
        ReactiveUser user = new ReactiveUser()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"));

        Mono<List<ReactiveUser>> userListMono = ReactiveOrganization.get(1)
                .flatMap(organization ->
                        user.insert().flatMap(organization::addUser).then(organization.getUserList())
                );

        StepVerifier.create(userListMono)
                .assertNext(userList -> assertAll(
                                () -> assertEquals(userList.get(0).getId(), 1),
                                () -> assertEquals(userList.get(0).getName(), "Robin Castillo")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void addMany() {
        organizationTableInsert();
        ReactiveUser user1 = new ReactiveUser()
                .setId((int) users.get(0).get("id"))
                .setName((String) users.get(0).get("name"))
                .setLogin((String) users.get(0).get("login"))
                .setPassword((String) users.get(0).get("password"))
                .setAge((int) users.get(0).get("age"));
        ReactiveUser user2 = new ReactiveUser()
                .setId((int) users.get(1).get("id"))
                .setName((String) users.get(1).get("name"))
                .setLogin((String) users.get(1).get("login"))
                .setPassword((String) users.get(1).get("password"))
                .setAge((int) users.get(1).get("age"));

        Mono<List<ReactiveUser>> userListMono = ReactiveOrganization.get(1)
                .flatMap(organization ->
                        Flux.concat(user1.insert(), user2.insert()).collectList().flatMap(organization::addUserList).then(organization.getUserList())
                );

        StepVerifier.create(userListMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.size(), 2),
                                () -> assertEquals(userList.get(0).getName(), "Robin Castillo"),
                                () -> assertEquals(userList.get(1).getName(), "Kelly Villarreal")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void addManyByJoin() {
        userTableInsert();
        ReactiveRole role1 = new ReactiveRole()
                .setId((int) roles.get(0).get("id"))
                .setName((String) roles.get(0).get("name"));

        ReactiveRole role2 = new ReactiveRole()
                .setId((int) roles.get(1).get("id"))
                .setName((String) roles.get(1).get("name"));

        Mono<List<ReactiveRole>> roleListMono = ReactiveUser.get(1)
                .flatMap(user ->
                        Flux.concat(role1.insert(), role2.insert()).collectList().flatMap(user::addRoleList).then(user.getRoleList())
                );

        StepVerifier.create(roleListMono)
                .assertNext(roleList ->
                        assertAll(
                                () -> assertEquals(roleList.size(), 2),
                                () -> assertEquals(roleList.get(0).getName(), "User"),
                                () -> assertEquals(roleList.get(1).getName(), "Admin")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void removeOne() {
        tableInsert();
        Mono<List<ReactiveUser>> userListMono = ReactiveOrganization.get(3)
                .flatMap(organization ->
                        ReactiveUser.get(1).flatMap(organization::removeUser).then(organization.getUserList())
                );

        StepVerifier.create(userListMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.size(), 1),
                                () -> assertEquals(userList.get(0).getId(), 2),
                                () -> assertEquals(userList.get(0).getName(), "Kelly Villarreal")
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void removeMany() {
        tableInsert();
        Mono<List<ReactiveUser>> userListMono = ReactiveOrganization.get(3)
                .flatMap(organization ->
                        Flux.concat(ReactiveUser.get(1), ReactiveUser.get(2)).collectList().flatMap(organization::removeUserList).then(organization.getUserList())
                );

        StepVerifier.create(userListMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.size(), 0)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void removeManyByJoin() {
        tableInsert();
        Mono<List<ReactiveRole>> userListMono = ReactiveUser.get(1)
                .flatMap(user ->
                        Flux.concat(ReactiveRole.get(1), ReactiveRole.get(2)).collectList().flatMap(user::removeRoleList).then(user.getRoleList())
                );

        StepVerifier.create(userListMono)
                .assertNext(userList ->
                        assertAll(
                                () -> assertEquals(userList.size(), 0)
                        )
                )
                .expectComplete()
                .verify();
    }
}
