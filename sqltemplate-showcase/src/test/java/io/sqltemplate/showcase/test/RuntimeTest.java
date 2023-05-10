package io.sqltemplate.showcase.test;

import io.sqltemplate.showcase.templates.User;
import io.sqltemplate.showcase.templates.UserTemplate;
import io.sqltemplate.spi.handler.Templates;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RuntimeTest {

    @Test
    void testMonoQueryList() {
        UserTemplate userTemplate = Templates.get(UserTemplate.class);
        List<User> userList = userTemplate.getUserListMono("n1").block();
        System.out.println(userList);
    }
}
