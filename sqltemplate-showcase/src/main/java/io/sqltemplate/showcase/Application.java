package io.sqltemplate.showcase;

import io.sqltemplate.runtime.handler.RuntimeTemplateProvider;
import io.sqltemplate.spi.handler.TemplateProvider;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private final TemplateProvider templateProvider = RuntimeTemplateProvider.getInstance();

    public static void main(String[] arg) {
//        new Application().testInsert();
//        new Application().testInsert();
        List<String> varList = new ArrayList<>();
        Var var1 = new Var();
        Var var2 = new Var();
        var1.setName("var1");
        var1.setValue("test");
        var2.setName("var2");
        var2.setValue("test2");
        STGroupFile stGroupFile = new STGroupFile("stg/test.stg");
        stGroupFile.registerRenderer(Var.class, new VarRenderer(varList));
        ST st = stGroupFile.getInstanceOf("testVar");
        st.add("_var1", var1);
        st.add("_var2", var2);
        System.out.println(st.render());
        varList.forEach(System.out::println);
    }


//    @Transactional
//    void testInsert() {
//        UserTemplate userTemplate = templateProvider.getTemplate(UserTemplate.class);
//        long l1 = userTemplate.insertUser("Robin Castillo", "castillo", "96954");
//        long l2 = userTemplate.insertUser("Kelly Villarreal", "villarreal", "54368");
//        long l3 = userTemplate.insertUser("Maliha England", "england", "68925");
//        long l4 = userTemplate.insertUser("Neha Chambers", "chambers", "47502");
//    }
}
