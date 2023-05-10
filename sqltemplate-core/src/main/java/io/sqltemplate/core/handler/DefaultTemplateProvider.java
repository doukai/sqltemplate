package io.sqltemplate.core.handler;

import io.sqltemplate.spi.handler.TemplateProvider;

import java.lang.reflect.InvocationTargetException;

public class DefaultTemplateProvider implements TemplateProvider {

    @SuppressWarnings("unchecked")
    public <T> T getTemplate(Class<T> tClass) {
        try {
            return (T) Class.forName(tClass.getName() + "Impl").getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
