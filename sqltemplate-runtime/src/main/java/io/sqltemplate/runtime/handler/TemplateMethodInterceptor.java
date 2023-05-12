package io.sqltemplate.runtime.handler;

import com.google.common.base.CaseFormat;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.InstanceType;
import io.sqltemplate.spi.annotation.Param;
import io.sqltemplate.spi.annotation.Template;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.*;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemplateMethodInterceptor {

    @RuntimeType
    public static Object interceptor(@This Object instance, @Origin Method method, @AllArguments Object[] args) throws Exception {
        Template templateAnnotation = method.getDeclaringClass().getAnnotation(Template.class);
        String templateName = templateAnnotation.value();

        String instanceName = method.getName();
        Instance instanceAnnotation = method.getAnnotation(Instance.class);
        InstanceType instanceType = InstanceType.QUERY;
        if (instanceAnnotation != null) {
            instanceName = instanceAnnotation.value();
            instanceType = instanceAnnotation.type();
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
            if (instanceType.equals(InstanceType.QUERY)) {
                return new JDBCAdapter<Object>(templateName, instanceName, params) {
                    @Override
                    protected Object map(Map<String, Object> result) {
                        return mapToObject(result, returnType);
                    }
                }.query();
            } else {
                return new JDBCAdapter<>(templateName, instanceName, params).update();
            }
        } else {
            ParameterizedType argumentType = (ParameterizedType) returnTypeArguments[0];
            if (returnType.isAssignableFrom(Map.class)) {
                return new JDBCAdapter<>(templateName, instanceName, params).query();
            } else if (returnType.isAssignableFrom(List.class)) {
                if (((Class<?>) argumentType.getRawType()).isAssignableFrom(Map.class)) {
                    return new JDBCAdapter<>(templateName, instanceName, params).queryList();
                } else {
                    return new JDBCAdapter<Object>(templateName, instanceName, params) {
                        @Override
                        protected Object map(Map<String, Object> result) {
                            return mapToObject(result, (Class<?>) argumentType.getRawType());
                        }
                    }.queryList();
                }
            } else if (returnType.isAssignableFrom(Mono.class)) {
                if (((Class<?>) argumentType.getRawType()).isAssignableFrom(Map.class)) {
                    return new R2DBCAdapter<>(templateName, instanceName, params).query();
                }else  if (((Class<?>) argumentType.getRawType()).isAssignableFrom(List.class)) {
                    Type argumentTypeArgumentType = argumentType.getActualTypeArguments()[0];
                    if (argumentTypeArgumentType instanceof ParameterizedType) {
                        return new R2DBCAdapter<>(templateName, instanceName, params).queryList();
                    } else {
                        Class<?> argumentTypeArgumentClass = (Class<?>) argumentTypeArgumentType;
                        return new R2DBCAdapter<Object>(templateName, instanceName, params) {
                            @Override
                            protected Object map(Map<String, Object> result) {
                                return mapToObject(result, argumentTypeArgumentClass);
                            }
                        }.queryList();
                    }
                } else {
                    if (instanceType.equals(InstanceType.QUERY)) {
                        return new R2DBCAdapter<Object>(templateName, instanceName, params) {
                            @Override
                            protected Object map(Map<String, Object> result) {
                                return mapToObject(result, (Class<?>) argumentType.getRawType());
                            }
                        }.query();
                    } else {
                        return new R2DBCAdapter<>(templateName, instanceName, params).update();
                    }
                }
            } else if (returnType.isAssignableFrom(Flux.class)) {
                if (((Class<?>) argumentType.getRawType()).isAssignableFrom(Map.class)) {
                    return new R2DBCAdapter<>(templateName, instanceName, params).queryFlux();
                } else {
                    return new R2DBCAdapter<Object>(templateName, instanceName, params) {
                        @Override
                        protected Object map(Map<String, Object> result) {
                            return mapToObject(result, (Class<?>) argumentType.getRawType());
                        }
                    }.queryFlux();
                }
            }
        }
        return null;
    }

    private static Object mapToObject(Map<String, Object> result, Class<?> returnType) {
        try {
            if (returnType.isPrimitive() ||
                    returnType.isAssignableFrom(Boolean.class) ||
                    returnType.isAssignableFrom(Character.class) ||
                    returnType.isAssignableFrom(Number.class) ||
                    returnType.isAssignableFrom(String.class)) {
                Iterator<?> iterator = result.values().iterator();
                if (iterator.hasNext()) {
                    return returnType.cast(iterator.next());
                }
                return null;
            } else if (returnType.isAssignableFrom(Map.class)) {
                return result;
            } else {
                Object o = returnType.getConstructor().newInstance();
                for (Method method : returnType.getMethods()) {
                    if (method.getName().startsWith("set")) {
                        String filedName = getFiledNameBySetterName(method.getName());
                        method.invoke(o, result.get(filedName) != null ? method.getParameters()[0].getType().cast(result.get(filedName)) : null);
                    }
                }
                return o;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFiledNameBySetterName(String methodName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName.replaceFirst("set", ""));
    }
}
