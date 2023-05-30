package io.sqltemplate.runtime.handler;

import com.google.auto.service.AutoService;
import io.sqltemplate.core.jdbc.DefaultJDBCAdapter;
import io.sqltemplate.runtime.handler.interceptor.*;
import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.InstanceType;
import io.sqltemplate.spi.annotation.Param;
import io.sqltemplate.spi.annotation.Template;
import io.sqltemplate.spi.handler.TemplateProvider;
import jakarta.transaction.Transactional;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AutoService(TemplateProvider.class)
public class RuntimeTemplateProvider implements TemplateProvider {

    private final Map<String, Object> templateInstanceCache = new ConcurrentHashMap<>();

    private final TypeCache<Class<?>> cache = new TypeCache<>();

    private final ByteBuddy byteBuddy = new ByteBuddy();

    private final RuntimeAdapterProvider adapterProvider = RuntimeAdapterProvider.getInstance();

    private static class RuntimeTemplateProviderHolder {
        private static final RuntimeTemplateProvider INSTANCE = new RuntimeTemplateProvider();
    }

    public static RuntimeTemplateProvider getInstance() {
        return RuntimeTemplateProvider.RuntimeTemplateProviderHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTemplate(Class<T> templateClass) {
        return (T) templateInstanceCache
                .computeIfAbsent(templateClass.getCanonicalName(),
                        key -> {
                            try {
                                return cache.findOrInsert(templateClass.getClassLoader(), templateClass, () -> makeTemplate(templateClass)).newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> makeTemplate(Class<T> templateClass) {
        DynamicType.Builder<T> builder = (DynamicType.Builder<T>) byteBuddy
                .subclass(Object.class)
                .implement(templateClass)
                .name(templateClass.getCanonicalName() + "RuntimeImpl");

        for (Method method : templateClass.getMethods()) {
            Template templateAnnotation = method.getDeclaringClass().getAnnotation(Template.class);
            String templateName = templateAnnotation.value();

            String instanceName = method.getName();
            Instance instanceAnnotation = method.getAnnotation(Instance.class);
            InstanceType instanceType = InstanceType.QUERY;
            if (instanceAnnotation != null) {
                if (!instanceAnnotation.value().equals("")) {
                    instanceName = instanceAnnotation.value();
                }
                instanceType = instanceAnnotation.type();
            }

            Transactional transactionalAnnotation = method.getAnnotation(Transactional.class);

            String[] argumentNames = Arrays.stream(method.getParameters())
                    .map(parameter -> {
                                String paramName = parameter.getName();
                                Param param = parameter.getAnnotation(Param.class);
                                if (param != null && !param.value().equals("")) {
                                    paramName = param.value();
                                }
                                return paramName;
                            }
                    )
                    .toArray(String[]::new);

            Class<?> returnType = method.getReturnType();
            if (returnType.isPrimitive()) {
                if (instanceType.equals(InstanceType.QUERY)) {
                    QueryInterceptor interceptor;
                    if (transactionalAnnotation != null) {
                        interceptor = new QueryInterceptor(adapterProvider.getJDBCAdapter(returnType), templateName, instanceName, argumentNames, transactionalAnnotation);
                    } else {
                        interceptor = new QueryInterceptor(adapterProvider.getJDBCAdapter(returnType), templateName, instanceName, argumentNames);
                    }
                    builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                } else {
                    UpdateInterceptor interceptor;
                    if (transactionalAnnotation != null) {
                        interceptor = new UpdateInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                    } else {
                        interceptor = new UpdateInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                    }
                    builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                }
            } else {
                Type genericReturnType = method.getGenericReturnType();
                if (!(genericReturnType instanceof ParameterizedType) || ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments().length == 0) {
                    if (instanceType.equals(InstanceType.QUERY)) {
                        QueryInterceptor interceptor;
                        if (transactionalAnnotation != null) {
                            interceptor = new QueryInterceptor(adapterProvider.getJDBCAdapter(returnType), templateName, instanceName, argumentNames, transactionalAnnotation);
                        } else {
                            interceptor = new QueryInterceptor(adapterProvider.getJDBCAdapter(returnType), templateName, instanceName, argumentNames);
                        }
                        builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                    } else {
                        UpdateInterceptor interceptor;
                        if (transactionalAnnotation != null) {
                            interceptor = new UpdateInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                        } else {
                            interceptor = new UpdateInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                        }
                        builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                    }
                } else {
                    if (returnType.isAssignableFrom(Map.class)) {
                        QueryInterceptor interceptor;
                        if (transactionalAnnotation != null) {
                            interceptor = new QueryInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                        } else {
                            interceptor = new QueryInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                        }
                        builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                    } else if (returnType.isAssignableFrom(List.class)) {
                        Type argumentType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                        if (argumentType instanceof ParameterizedType && ((Class<?>) ((ParameterizedType) argumentType).getRawType()).isAssignableFrom(Map.class)) {
                            QueryListInterceptor interceptor;
                            if (transactionalAnnotation != null) {
                                interceptor = new QueryListInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                            } else {
                                interceptor = new QueryListInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                            }
                            builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                        } else if (argumentType instanceof Class<?>) {
                            QueryListInterceptor interceptor;
                            if (transactionalAnnotation != null) {
                                interceptor = new QueryListInterceptor(adapterProvider.getJDBCAdapter((Class<?>) argumentType), templateName, instanceName, argumentNames, transactionalAnnotation);
                            } else {
                                interceptor = new QueryListInterceptor(adapterProvider.getJDBCAdapter((Class<?>) argumentType), templateName, instanceName, argumentNames);
                            }
                            builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                        }
                    } else if (returnType.isAssignableFrom(Mono.class)) {
                        Type argumentType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                        if (argumentType instanceof ParameterizedType && ((Class<?>) ((ParameterizedType) argumentType).getRawType()).isAssignableFrom(Map.class)) {
                            QueryMonoInterceptor interceptor;
                            if (transactionalAnnotation != null) {
                                interceptor = new QueryMonoInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                            } else {
                                interceptor = new QueryMonoInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                            }
                            builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                        } else if (argumentType instanceof ParameterizedType && ((Class<?>) ((ParameterizedType) argumentType).getRawType()).isAssignableFrom(List.class)) {
                            Type argumentTypeArgumentType = ((ParameterizedType) argumentType).getActualTypeArguments()[0];
                            if (argumentTypeArgumentType instanceof ParameterizedType) {
                                QueryMonoListInterceptor interceptor;
                                if (transactionalAnnotation != null) {
                                    interceptor = new QueryMonoListInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                                } else {
                                    interceptor = new QueryMonoListInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                                }
                                builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                            } else {
                                QueryMonoListInterceptor interceptor;
                                if (transactionalAnnotation != null) {
                                    interceptor = new QueryMonoListInterceptor(adapterProvider.getR2DBCAdapter((Class<?>) argumentTypeArgumentType), templateName, instanceName, argumentNames, transactionalAnnotation);
                                } else {
                                    interceptor = new QueryMonoListInterceptor(adapterProvider.getR2DBCAdapter((Class<?>) argumentTypeArgumentType), templateName, instanceName, argumentNames);
                                }
                                builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                            }
                        } else {
                            if (argumentType instanceof Class<?> && instanceType.equals(InstanceType.QUERY)) {
                                QueryMonoInterceptor interceptor;
                                if (transactionalAnnotation != null) {
                                    interceptor = new QueryMonoInterceptor(adapterProvider.getR2DBCAdapter((Class<?>) argumentType), templateName, instanceName, argumentNames, transactionalAnnotation);
                                } else {
                                    interceptor = new QueryMonoInterceptor(adapterProvider.getR2DBCAdapter((Class<?>) argumentType), templateName, instanceName, argumentNames);
                                }
                                builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                            } else {
                                UpdateMonoInterceptor interceptor;
                                if (transactionalAnnotation != null) {
                                    interceptor = new UpdateMonoInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                                } else {
                                    interceptor = new UpdateMonoInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                                }
                                builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                            }
                        }
                    } else if (returnType.isAssignableFrom(Flux.class)) {
                        Type argumentType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                        if (argumentType instanceof ParameterizedType && ((Class<?>) ((ParameterizedType) argumentType).getRawType()).isAssignableFrom(Map.class)) {
                            QueryFluxInterceptor interceptor;
                            if (transactionalAnnotation != null) {
                                interceptor = new QueryFluxInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames, transactionalAnnotation);
                            } else {
                                interceptor = new QueryFluxInterceptor(new DefaultJDBCAdapter<>(), templateName, instanceName, argumentNames);
                            }
                            builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                        } else if (argumentType instanceof Class<?>) {
                            QueryFluxInterceptor interceptor;
                            if (transactionalAnnotation != null) {
                                interceptor = new QueryFluxInterceptor(adapterProvider.getR2DBCAdapter((Class<?>) argumentType), templateName, instanceName, argumentNames, transactionalAnnotation);
                            } else {
                                interceptor = new QueryFluxInterceptor(adapterProvider.getR2DBCAdapter((Class<?>) argumentType), templateName, instanceName, argumentNames);
                            }
                            builder = builder.define(method).intercept(MethodDelegation.to(interceptor));
                        }
                    }
                }
            }
        }
        return builder
                .make()
                .load(templateClass.getClassLoader())
                .getLoaded();
    }
}
