package io.sqltemplate.showcase.test;

import io.sqltemplate.runtime.handler.RuntimeTemplateProvider;
import io.sqltemplate.showcase.test.dto.User;
import io.sqltemplate.showcase.test.templates.UserTemplate;
import io.sqltemplate.spi.handler.TemplateProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static io.sqltemplate.showcase.test.Setup.tableClear;
import static io.sqltemplate.showcase.test.Setup.tableInit;
import static io.sqltemplate.showcase.test.Setup.tableInsert;
import static io.sqltemplate.showcase.test.Setup.users;
import static org.junit.jupiter.api.Assertions.*;

public class RuntimeTemplateTest {

    private final TemplateProvider templateProvider = RuntimeTemplateProvider.getInstance();

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
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        int rows = 0;
        for (Map<String, Object> user : users) {
            rows += userTemplate.insertUser((int) user.get("id"), (String) user.get("name"), (String) user.get("login"), (String) user.get("password"), (int) user.get("age"));
        }
        assertEquals(rows, 4);
    }

    @Test
    void insertMono() {
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        StepVerifier.create(
                        Flux.fromIterable(users)
                                .flatMap(user -> userTemplate.insertUserMono((int) user.get("id"), (String) user.get("name"), (String) user.get("login"), (String) user.get("password"), (int) user.get("age")))
                )
                .expectNext(1L)
                .expectNext(1L)
                .expectNext(1L)
                .expectNext(1L)
                .expectComplete()
                .verify();
    }

    @Test
    void select() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        User user1 = userTemplate.getUser("1");
        User user2 = userTemplate.getUser("2");
        User user3 = userTemplate.getUser("3");
        User user4 = userTemplate.getUser("4");
        assertAll(
                () -> assertEquals(user1.getName(), "Robin Castillo"),
                () -> assertEquals(user1.getLogin(), "castillo"),
                () -> assertEquals(user1.getPassword(), "96954"),
                () -> assertEquals(user1.getAge(), 9),
                () -> assertEquals(user2.getName(), "Kelly Villarreal"),
                () -> assertEquals(user2.getLogin(), "villarreal"),
                () -> assertEquals(user2.getPassword(), "54368"),
                () -> assertEquals(user2.getAge(), 18),
                () -> assertEquals(user3.getName(), "Malia England"),
                () -> assertEquals(user3.getLogin(), "england"),
                () -> assertEquals(user3.getPassword(), "68925"),
                () -> assertEquals(user3.getAge(), 27),
                () -> assertEquals(user4.getName(), "Neha Chambers"),
                () -> assertEquals(user4.getLogin(), "chambers"),
                () -> assertEquals(user4.getPassword(), "47502"),
                () -> assertEquals(user4.getAge(), 36)
        );
    }

    @Test
    void selectMono() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        StepVerifier.create(
                        Flux.concat(
                                userTemplate.getUserMono("1"),
                                userTemplate.getUserMono("2"),
                                userTemplate.getUserMono("3"),
                                userTemplate.getUserMono("4")
                        )
                )
                .assertNext(user -> assertAll(
                                () -> assertEquals(user.getName(), "Robin Castillo"),
                                () -> assertEquals(user.getLogin(), "castillo"),
                                () -> assertEquals(user.getPassword(), "96954"),
                                () -> assertEquals(user.getAge(), 9)
                        )
                )
                .assertNext(user -> assertAll(
                                () -> assertEquals(user.getName(), "Kelly Villarreal"),
                                () -> assertEquals(user.getLogin(), "villarreal"),
                                () -> assertEquals(user.getPassword(), "54368"),
                                () -> assertEquals(user.getAge(), 18)
                        )
                )
                .assertNext(user -> assertAll(
                                () -> assertEquals(user.getName(), "Malia England"),
                                () -> assertEquals(user.getLogin(), "england"),
                                () -> assertEquals(user.getPassword(), "68925"),
                                () -> assertEquals(user.getAge(), 27)
                        )
                )
                .assertNext(user -> assertAll(
                                () -> assertEquals(user.getName(), "Neha Chambers"),
                                () -> assertEquals(user.getLogin(), "chambers"),
                                () -> assertEquals(user.getPassword(), "47502"),
                                () -> assertEquals(user.getAge(), 36)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void selectList() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        List<User> userList = userTemplate.getUserListByName("%la%");

        assertEquals(userList.size(), 2);
    }

    @Test
    void selectListMono() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        StepVerifier.create(
                        userTemplate.getUserListByNameMono("%la%")
                )
                .assertNext(userList -> assertEquals(userList.size(), 2))
                .expectComplete()
                .verify();
    }

    @Test
    void selectFlux() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        StepVerifier.create(
                        userTemplate.getUserListByNameFlux("%la%")
                )
                .assertNext(user -> assertAll(
                                () -> assertEquals(user.getName(), "Kelly Villarreal"),
                                () -> assertEquals(user.getLogin(), "villarreal"),
                                () -> assertEquals(user.getPassword(), "54368"),
                                () -> assertEquals(user.getAge(), 18)
                        )
                )
                .assertNext(user -> assertAll(
                                () -> assertEquals(user.getName(), "Malia England"),
                                () -> assertEquals(user.getLogin(), "england"),
                                () -> assertEquals(user.getPassword(), "68925"),
                                () -> assertEquals(user.getAge(), 27)
                        )
                )
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);

        long updateCount1 = userTemplate.updateUserNameById("1", "Robert Castillo");
        long updateCount2 = userTemplate.updateUserNameById("3", "Mary England");
        User user1 = userTemplate.getUser("1");
        User user3 = userTemplate.getUser("3");

        assertAll(
                () -> assertEquals(updateCount1, 1),
                () -> assertEquals(updateCount2, 1),
                () -> assertEquals(user1.getName(), "Robert Castillo"),
                () -> assertEquals(user3.getName(), "Mary England")
        );
    }

    @Test
    void updateMono() {
        tableInsert();
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);

        StepVerifier.create(
                        Flux.concat(
                                userTemplate.updateUserNameByIdMono("1", "Robert Castillo"),
                                userTemplate.updateUserNameByIdMono("3", "Mary England")
                        )
                )
                .expectNext(1L)
                .expectNext(1L)
                .expectComplete()
                .verify();

        StepVerifier.create(
                        Flux.concat(
                                userTemplate.getUserMono("1"),
                                userTemplate.getUserMono("3")
                        )
                )
                .assertNext(user -> assertEquals(user.getName(), "Robert Castillo"))
                .assertNext(user -> assertEquals(user.getName(), "Mary England"))
                .expectComplete()
                .verify();
    }
}
