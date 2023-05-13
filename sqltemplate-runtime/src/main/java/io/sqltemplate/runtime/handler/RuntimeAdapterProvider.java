package io.sqltemplate.runtime.handler;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import io.sqltemplate.core.adapter.Adapter;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import javassist.*;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Map;

public class RuntimeAdapterProvider {

    private final ClassPool classPool = ClassPool.getDefault();

    public <T> Adapter<?> getJDBCAdapter(Class<T> entityClass) {
        try {
            return getJDBCAdapterClass(entityClass).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Adapter<?> getR2DBCAdapter(Class<T> entityClass) {
        try {
            return getR2DBCAdapterClass(entityClass).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
        String entityAdapterName = entityClass.getCanonicalName() + adapterClass.getSimpleName();
        try {
            CtClass AdapterCtClass = classPool.getOrNull(entityAdapterName);
            if (AdapterCtClass != null) {
                return (Class<? extends Adapter<?>>) AdapterCtClass.toClass();
            }
            return makeAdapterClass(entityClass, reactive);
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
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(Map.class, String.class, Object.class), "result")
                .returns(entityClass)
                .addStatement("$T entity = new $T()", ClassName.get(entityClass), ClassName.get(entityClass));

        Arrays.stream(entityClass.getMethods())
                .filter(method -> method.getName().startsWith("set"))
                .map(method ->
                        CodeBlock.of("entity.$L(result.get($S) != null ? ($T) result.get($S) : null)",
                                method.getName(),
                                getFiledNameBySetterName(method.getName()),
                                ClassName.get(method.getParameters()[0].getType()),
                                getFiledNameBySetterName(method.getName())
                        )
                )
                .forEach(builder::addStatement);

        builder.addStatement("return entity", ClassName.get(entityClass), ClassName.get(entityClass));
        return builder.build();
    }

    private String getFiledNameBySetterName(String methodName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName.replaceFirst("set", ""));
    }
}
