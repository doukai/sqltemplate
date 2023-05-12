package io.sqltemplate.showcase.test;

import io.sqltemplate.showcase.entity.UserRecord;
import io.sqltemplate.showcase.templates.Sex;
import io.sqltemplate.showcase.templates.User;
import io.sqltemplate.showcase.templates.UserTemplate;
import io.sqltemplate.spi.handler.Templates;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RuntimeTest {

    @Test
    void testMonoQueryList() {
        UserTemplate userTemplate = Templates.get(UserTemplate.class);
        List<User> userList = userTemplate.getUserListMono("n1").block();
        System.out.println(userList);
    }

    @Test
    void testActiveRecord() {
//        new UserRecord().setName("1").setPassword("rp1").setLogin("rl1").setAge(12).setSex(Sex.MAN).insert();
        AtomicInteger integer = new AtomicInteger(0);
        System.out.println(integer.getAndIncrement());
    }
}
