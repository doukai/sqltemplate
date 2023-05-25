package io.sqltemplate.showcase.test;

import io.sqltemplate.runtime.handler.RuntimeTemplateProvider;
import io.sqltemplate.showcase.templates.UserTemplate;
import io.sqltemplate.spi.handler.TemplateProvider;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuntimeTemplateTest {

    private final TemplateProvider templateProvider = RuntimeTemplateProvider.getInstance();

    private final List<Map<String, Object>> users = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("name", "Robin Castillo");
            put("login", "castillo");
            put("password", "96954");
            put("age", "");
        }});
        add(new HashMap<String, Object>() {{
            put("name", "Kelly Villarreal");
            put("login", "villarreal");
            put("password", "54368");
            put("age", "");
        }});
        add(new HashMap<String, Object>() {{
            put("name", "Maliha England");
            put("login", "england");
            put("password", "68925");
            put("age", "");
        }});
        add(new HashMap<String, Object>() {{
            put("name", "Neha Chambers");
            put("login", "chambers");
            put("password", "47502");
            put("age", "");
        }});
    }};


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
