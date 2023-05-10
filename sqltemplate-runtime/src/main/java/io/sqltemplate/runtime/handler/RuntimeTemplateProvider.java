package io.sqltemplate.runtime.handler;

import com.google.auto.service.AutoService;
import io.sqltemplate.spi.handler.TemplateProvider;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

@AutoService(TemplateProvider.class)
public class RuntimeTemplateProvider implements TemplateProvider {

    private final TypeCache<Class<?>> cache = new TypeCache<>();

    @SuppressWarnings("unchecked")
    public <T> T getTemplate(Class<T> templateClass) {
        try {
            return (T) cache.findOrInsert(templateClass.getClassLoader(), templateClass, () -> createTemplate(templateClass)).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> createTemplate(Class<T> templateClass) {
        return (Class<? extends T>) new ByteBuddy()
                .subclass(Object.class)
                .implement(templateClass)
                .method(ElementMatchers.isDeclaredBy(templateClass))
                .intercept(MethodDelegation.to(TemplateMethodInterceptor.class))
                .make()
                .load(templateClass.getClassLoader())
                .getLoaded();
    }
}
