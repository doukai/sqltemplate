package io.sqltemplate.showcase.test;

import io.sqltemplate.runtime.handler.RuntimeTemplateProvider;
import io.sqltemplate.showcase.templates.UserTemplate;
import io.sqltemplate.spi.handler.TemplateProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuntimeTemplateTest {

    private final TemplateProvider templateProvider = RuntimeTemplateProvider.getInstance();


    @Test
    void testInsert() {
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        long l1 = userTemplate.insertUser("Robin Castillo", "castillo", "96954");
        long l2 = userTemplate.insertUser("Kelly Villarreal", "villarreal", "54368");
        long l3 = userTemplate.insertUser("Maliha England", "england", "68925");
        long l4 = userTemplate.insertUser("Neha Chambers", "chambers", "47502");
        assertAll("numbers",
                () -> assertEquals(l1, 1),
                () -> assertEquals(l2, 1),
                () -> assertEquals(l3, 1),
                () -> assertEquals(l4, 1)
        );
    }
}
