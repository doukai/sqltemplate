package io.sqltemplate.runtime.handler;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.Param;
import io.sqltemplate.spi.annotation.Template;
import io.sqltemplate.spi.handler.TemplateProvider;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .intercept(MethodDelegation.to(MethodInterceptor.class))
                .make()
                .load(templateClass.getClassLoader())
                .getLoaded();
    }

    public static class MethodInterceptor {

        @RuntimeType
        public static Object interceptor(@This Object instance, @Origin Method method, @AllArguments Object[] args) throws Exception {
            Template templateAnnotation = method.getDeclaringClass().getAnnotation(Template.class);
            String templateName = templateAnnotation.value();

            String instanceName = method.getName();
            Instance instanceAnnotation = method.getAnnotation(Instance.class);
            if (instanceAnnotation != null) {
                instanceName = instanceAnnotation.value();
            }

            Parameter[] parameters = method.getParameters();
            Map<String, Object> params = IntStream.range(0, parameters.length)
                    .mapToObj(index -> {
                                Parameter parameter = parameters[index];
                                String paramName = parameter.getName();
                                Param param = parameter.getAnnotation(Param.class);
                                if (param != null && !param.value().equals("")) {
                                    paramName = param.value();
                                }
                                return new AbstractMap.SimpleEntry<>(paramName, args[index]);
                            }
                    )
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Class<?> returnType = method.getReturnType();
            Type[] returnTypeArguments = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
            if (returnTypeArguments.length == 0) {
                return new JDBCAdapter<Object>(templateName, instanceName, params) {
                    @Override
                    protected Object map(Map<String, Object> result) {
                        return RuntimeTemplateProvider.map(result, returnType);
                    }
                }.query();
            } else {
                ParameterizedType argumentType = (ParameterizedType) returnTypeArguments[0];
                if (returnType.isAssignableFrom(List.class)) {
                    return new JDBCAdapter<Object>(templateName, instanceName, params) {
                        @Override
                        protected Object map(Map<String, Object> result) {
                            return RuntimeTemplateProvider.map(result, (Class<?>) argumentType.getRawType());
                        }
                    }.queryList();
                } else if (returnType.isAssignableFrom(Mono.class)) {
                    if (((Class<?>) argumentType.getRawType()).isAssignableFrom(List.class)) {
                        Class<?> argumentTypeArgumentClass = (Class<?>) argumentType.getActualTypeArguments()[0];
                        return new R2DBCAdapter<Object>(templateName, instanceName, params) {
                            @Override
                            protected Object map(Map<String, Object> result) {
                                return RuntimeTemplateProvider.map(result, argumentTypeArgumentClass);
                            }
                        }.queryList();
                    } else {
                        return new R2DBCAdapter<Object>(templateName, instanceName, params) {
                            @Override
                            protected Object map(Map<String, Object> result) {
                                return RuntimeTemplateProvider.map(result, (Class<?>) argumentType.getRawType());
                            }
                        }.query();
                    }
                } else if (returnType.isAssignableFrom(Flux.class)) {
                    return new R2DBCAdapter<Object>(templateName, instanceName, params) {
                        @Override
                        protected Object map(Map<String, Object> result) {
                            return RuntimeTemplateProvider.map(result, (Class<?>) argumentType.getRawType());
                        }
                    }.queryFlux();
                }
            }
            return null;
        }
    }

    private static Object map(Map<String, Object> params, Class<?> returnType) {
        try {
            Object o = returnType.getConstructor().newInstance();
            for (Method method : returnType.getMethods()) {
                if (method.getName().startsWith("set")) {
                    String filedName = getFiledNameBySetterName(method.getName());
                    method.invoke(o, params.get(filedName) != null ? method.getParameters()[0].getType().cast(params.get(filedName)) : null);
                }
            }
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFiledNameBySetterName(String methodName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName.replaceFirst("set", ""));
    }
}
