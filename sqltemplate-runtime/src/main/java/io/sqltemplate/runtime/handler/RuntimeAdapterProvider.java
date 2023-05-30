package io.sqltemplate.runtime.handler;

import com.google.common.base.CaseFormat;
import com.google.common.primitives.Primitives;
import com.squareup.javapoet.*;
import io.sqltemplate.core.adapter.Adapter;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import javassist.*;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeAdapterProvider {

    private final Map<String, Object> adapterInstanceCache = new ConcurrentHashMap<>();

    private final ClassPool classPool = ClassPool.getDefault();

    private static class RuntimeAdapterProviderHolder {
        private static final RuntimeAdapterProvider INSTANCE = new RuntimeAdapterProvider();
    }

    public static RuntimeAdapterProvider getInstance() {
        return RuntimeAdapterProvider.RuntimeAdapterProviderHolder.INSTANCE;
    }

    private RuntimeAdapterProvider() {
    }

    public <T> Adapter<?> getJDBCAdapter(Class<T> entityClass) {
        Class<?> returnClass = entityClass.isPrimitive() ? Primitives.wrap(entityClass) : entityClass;
        String entityAdapterName = returnClass.getCanonicalName() + JDBCAdapter.class.getSimpleName();
        return (Adapter<?>) adapterInstanceCache
                .computeIfAbsent(entityAdapterName,
                        key -> {
                            try {
                                return getJDBCAdapterClass(entityClass).newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public <T> Adapter<?> getR2DBCAdapter(Class<T> entityClass) {
        Class<?> returnClass = entityClass.isPrimitive() ? Primitives.wrap(entityClass) : entityClass;
        String entityAdapterName = returnClass.getCanonicalName() + R2DBCAdapter.class.getSimpleName();
        return (Adapter<?>) adapterInstanceCache
                .computeIfAbsent(entityAdapterName,
                        key -> {
                            try {
                                return getR2DBCAdapterClass(entityClass).newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    private <T> Class<? extends Adapter<?>> getJDBCAdapterClass(Class<T> entityClass) {
        return getAdapterClass(entityClass, false);
    }

    private <T> Class<? extends Adapter<?>> getR2DBCAdapterClass(Class<T> entityClass) {
        return getAdapterClass(entityClass, true);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends Adapter<?>> getAdapterClass(Class<T> entityClass, boolean reactive) {
        Class<?> adapterClass = reactive ? R2DBCAdapter.class : JDBCAdapter.class;
        Class<?> returnClass = entityClass.isPrimitive() ? Primitives.wrap(entityClass) : entityClass;
        String entityAdapterName = returnClass.getCanonicalName() + adapterClass.getSimpleName();

        try {
            CtClass adapterCtClass = classPool.getOrNull(entityAdapterName);
            if (adapterCtClass != null) {
                return (Class<? extends Adapter<?>>) adapterCtClass.toClass();
            }
            return makeAdapterClass(returnClass, reactive);
        } catch (CannotCompileException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends Adapter<?>> makeAdapterClass(Class<T> entityClass, boolean reactive) throws CannotCompileException, NotFoundException {
        Class<?> adapterClass = reactive ? R2DBCAdapter.class : JDBCAdapter.class;
        String entityAdapterName = entityClass.getCanonicalName() + adapterClass.getSimpleName();
        CtClass entityAdapter = classPool.makeClass(entityAdapterName, classPool.get(adapterClass.getCanonicalName()));

        CtMethod mapMethod = CtNewMethod.make(buildMapMethod(entityClass).toString(), entityAdapter);
        entityAdapter.addMethod(mapMethod);
        return (Class<? extends Adapter<?>>) entityAdapter.toClass();
    }

    private <T> MethodSpec buildMapMethod(Class<T> entityClass) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("map")
//                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Map.class, "result")
                .returns(Object.class);
        TypeName returnTypeName = TypeName.get(entityClass);
        if (returnTypeName instanceof ParameterizedTypeName) {
            returnTypeName = ((ParameterizedTypeName) returnTypeName).rawType;
        }
        if (entityClass.isPrimitive() ||
                entityClass.isAssignableFrom(Boolean.class) ||
                entityClass.isAssignableFrom(Character.class) ||
                entityClass.isAssignableFrom(Number.class) ||
                entityClass.isAssignableFrom(String.class)) {
            builder.addStatement("$T<?> iterator = result.values().iterator()", ClassName.get(Iterator.class))
                    .beginControlFlow("if (iterator.hasNext())")
                    .addStatement("return ($T) iterator.next()", returnTypeName)
                    .endControlFlow()
                    .addStatement("return null");
        } else if (entityClass.isAssignableFrom(Map.class)) {
            builder.addStatement("return result");
        } else {
            builder.addStatement("$T entity = new $T()", returnTypeName, returnTypeName);
            Arrays.stream(entityClass.getMethods())
                    .filter(method -> method.getModifiers() == java.lang.reflect.Modifier.PUBLIC)
                    .filter(method -> method.getName().startsWith("set"))
                    .map(method -> {
                                TypeName parameterTypeName = TypeName.get(method.getParameters()[0].getType());
                                if (parameterTypeName instanceof ParameterizedTypeName) {
                                    parameterTypeName = ((ParameterizedTypeName) parameterTypeName).rawType;
                                }
                                return CodeBlock.of("if(result.get($S) != null)entity.$L(($T) result.get($S))",
                                        getFiledNameBySetterName(method.getName()),
                                        method.getName(),
                                        parameterTypeName,
                                        getFiledNameBySetterName(method.getName())
                                );
                            }
                    )
                    .forEach(builder::addStatement);
            builder.addStatement("return entity");
        }
        return builder.build();
    }

    private String getFiledNameBySetterName(String methodName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName.replaceFirst("set", ""));
    }
}
