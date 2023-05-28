package io.sqltemplate.showcase.test;

import io.sqltemplate.runtime.handler.RuntimeTemplateProvider;
import io.sqltemplate.showcase.templates.UserTemplate;
import io.sqltemplate.spi.handler.TemplateProvider;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuntimeTemplateTest {

    private final TemplateProvider templateProvider = RuntimeTemplateProvider.getInstance();

    private final List<Map<String, Object>> users = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("name", "Robin Castillo");
            put("login", "castillo");
            put("password", "96954");
            put("age", 9);
        }});
        add(new HashMap<String, Object>() {{
            put("name", "Kelly Villarreal");
            put("login", "villarreal");
            put("password", "54368");
            put("age", 18);
        }});
        add(new HashMap<String, Object>() {{
            put("name", "Malia England");
            put("login", "england");
            put("password", "68925");
            put("age", 27);
        }});
        add(new HashMap<String, Object>() {{
            put("name", "Neha Chambers");
            put("login", "chambers");
            put("password", "47502");
            put("age", 36);
        }});
    }};

    @Test
    void testInsert() {
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        int rows = 0;
        for (Map<String, Object> user : users) {
            rows += userTemplate.insertUser((String) user.get("name"), (String) user.get("login"), (String) user.get("password"), (int) user.get("age"));
        }
        assertEquals(rows, 4);
    }
}
