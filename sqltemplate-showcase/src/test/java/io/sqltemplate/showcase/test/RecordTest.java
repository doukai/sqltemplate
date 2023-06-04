package io.sqltemplate.showcase.test;

import io.sqltemplate.showcase.dto.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.sqltemplate.showcase.test.Setup.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
//        User.insertAll(
//                users.stream()
//                        .map(user ->
//                                new User()
//                                        .setId((int) user.get("id"))
//                                        .setName((String) user.get("name"))
//                                        .setLogin((String) user.get("login"))
//                                        .setPassword((String) user.get("password"))
//                                        .setAge((int) user.get("age"))
//                        )
//                        .toArray(User[]::new)
//        );
//
//        assertEquals(User.all().size(), 4);
    }
}
