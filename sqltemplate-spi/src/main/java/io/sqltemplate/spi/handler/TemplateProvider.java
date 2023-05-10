package io.sqltemplate.spi.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.ServiceLoader;

public interface TemplateProvider {

    String DEFAULT_PROVIDER = "io.sqltemplate.core.handler.DefaultTemplateProvider";
    
    static TemplateProvider provider() {
        ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class);
        Iterator<TemplateProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        try {
            return (TemplateProvider) Class.forName(DEFAULT_PROVIDER).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    <T> T getTemplate(Class<T> tClass);
}
