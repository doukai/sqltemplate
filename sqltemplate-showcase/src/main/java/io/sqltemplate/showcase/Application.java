package io.sqltemplate.showcase;

import io.sqltemplate.runtime.handler.RuntimeTemplateProvider;
import io.sqltemplate.showcase.templates.UserTemplate;
import io.sqltemplate.spi.handler.TemplateProvider;
import jakarta.transaction.Transactional;

public class Application {

    private final TemplateProvider templateProvider = RuntimeTemplateProvider.getInstance();

    public static void main(String[] arg) {
        new Application().testInsert();
        new Application().testInsert();
    }


    @Transactional
    void testInsert() {
        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
        long l1 = userTemplate.insertUser("Robin Castillo", "castillo", "96954");
        long l2 = userTemplate.insertUser("Kelly Villarreal", "villarreal", "54368");
        long l3 = userTemplate.insertUser("Maliha England", "england", "68925");
        long l4 = userTemplate.insertUser("Neha Chambers", "chambers", "47502");
    }
}
