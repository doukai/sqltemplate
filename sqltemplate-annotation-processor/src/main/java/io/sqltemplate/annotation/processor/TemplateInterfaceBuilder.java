package io.sqltemplate.annotation.processor;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.InstanceType;
import io.sqltemplate.spi.annotation.Param;
import io.sqltemplate.spi.annotation.Template;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateInterfaceBuilder {

    private final Filer filer;
    private final Elements elementUtils;
    private final Types typeUtils;

    public TemplateInterfaceBuilder(ProcessingEnvironment processingEnv) {
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    public void write(TypeElement typeElement) throws IOException {
        buildTemplateInterface(typeElement).writeTo(filer);
    }

    private JavaFile buildTemplateInterface(TypeElement typeElement) {
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);

        TypeSpec.Builder builder = TypeSpec.classBuilder(ClassName.get(packageElement.getQualifiedName().toString(), typeElement.getSimpleName().toString() + "Impl"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(typeElement.asType())
                .addMethods(typeElement.getEnclosedElements()
                        .stream()
                        .filter(element -> element.getKind().equals(ElementKind.METHOD))
                        .map(element -> executableElementToMethodSpec((ExecutableElement) element))
                        .collect(Collectors.toList())
                );

        return JavaFile.builder(packageElement.getQualifiedName().toString(), builder.build()).build();
    }

    private MethodSpec executableElementToMethodSpec(ExecutableElement executableElement) {
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        Template templateAnnotation = typeElement.getAnnotation(Template.class);
        String templateName = templateAnnotation.value();

        String instanceName = executableElement.getSimpleName().toString();
        Instance instanceAnnotation = executableElement.getAnnotation(Instance.class);
        InstanceType type = InstanceType.QUERY;
        if (instanceAnnotation != null) {
            if (!instanceAnnotation.value().equals("")) {
                instanceName = instanceAnnotation.value();
            }
            type = instanceAnnotation.type();
        }

        Transactional.TxType txType = Transactional.TxType.REQUIRED;
        String[] rollbackOn = {};
        String[] dontRollbackOn = {};
        Transactional transactionalAnnotation = executableElement.getAnnotation(Transactional.class);
        if (transactionalAnnotation != null) {
            txType = transactionalAnnotation.value();
            rollbackOn = executableElement.getAnnotationMirrors().stream()
                    .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(Transactional.class.getCanonicalName()))
                    .flatMap(annotationMirror -> annotationMirror.getElementValues().entrySet().stream())
                    .filter(entry -> entry.getKey().getSimpleName().toString().equals("rollbackOn"))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .map(annotationValue -> ((List<?>) annotationValue.getValue()).stream().map(Object::toString).toArray(String[]::new))
                    .orElse(new String[]{});
            dontRollbackOn = executableElement.getAnnotationMirrors().stream()
                    .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(Transactional.class.getCanonicalName()))
                    .flatMap(annotationMirror -> annotationMirror.getElementValues().entrySet().stream())
                    .filter(entry -> entry.getKey().getSimpleName().toString().equals("dontRollbackOn"))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .map(annotationValue -> ((List<?>) annotationValue.getValue()).stream().map(Object::toString).toArray(String[]::new))
                    .orElse(new String[]{});
        }

        CodeBlock rollbackOnArray = Arrays.stream(rollbackOn)
                .map(rollbackOnClass -> CodeBlock.of("$L", rollbackOnClass))
                .collect(CodeBlock.joining(", ", "{", "}"));

        CodeBlock dontRollbackOnArray = Arrays.stream(dontRollbackOn)
                .map(dontRollbackOnClass -> CodeBlock.of("$L", dontRollbackOnClass))
                .collect(CodeBlock.joining(", ", "{", "}"));

        TypeName returnTypeName = ClassName.get(executableElement.getReturnType());

        MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addParameters(
                        executableElement.getParameters().stream()
                                .map(variableElement -> ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build())
                                .collect(Collectors.toList())
                )
                .returns(returnTypeName);

        CodeBlock.Builder paramsBlockBuilder = CodeBlock.builder();
        paramsBlockBuilder.add("$T<String, Object> params = new $T<String, Object>() {{\n", ClassName.get(Map.class), ClassName.get(HashMap.class)).indent();
        executableElement.getParameters().forEach(variableElement -> {
                    String paramName = variableElement.getSimpleName().toString();
                    Param param = variableElement.getAnnotation(Param.class);
                    if (param != null && !param.value().equals("")) {
                        paramName = param.value();
                    }
                    paramsBlockBuilder.add("put($S, $L);\n", paramName, variableElement.getSimpleName().toString());
                }
        );
        paramsBlockBuilder.unindent().add("}};\n");
        builder.addCode(paramsBlockBuilder.build());

        TypeMirror returnType = executableElement.getReturnType();
        if (returnType.getKind().isPrimitive()) {
            CodeBlock.Builder returnBuilder;
            if (type.equals(InstanceType.QUERY)) {
                returnBuilder = CodeBlock.builder()
                        .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L) {\n",
                                ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), TypeName.get(returnType)),
                                templateName,
                                instanceName,
                                ClassName.get(Transactional.TxType.class),
                                txType.name(),
                                rollbackOnArray,
                                dontRollbackOnArray
                        )
                        .indent()
                        .add("@$T\n", ClassName.get(Override.class))
                        .add("protected $T map($T<String, Object> result) {\n", TypeName.get(returnType), ClassName.get(Map.class))
                        .indent();

                entityBuilderCodeBlockList(typeUtils.asElement(returnType)).forEach(returnBuilder::add);

                returnBuilder
                        .unindent()
                        .add("}\n")
                        .unindent()
                        .add("}.query();\n");
            } else {
                returnBuilder = CodeBlock.builder()
                        .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).update();\n",
                                ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), returnType.getKind().isPrimitive() ? TypeName.get(returnType).box() : TypeName.get(returnType)),
                                templateName,
                                instanceName,
                                ClassName.get(Transactional.TxType.class),
                                txType.name(),
                                rollbackOnArray,
                                dontRollbackOnArray
                        );
            }
            builder.addCode(returnBuilder.build());
        } else {
            DeclaredType returnDeclaredType = (DeclaredType) executableElement.getReturnType();
            TypeElement returnTypeElement = (TypeElement) typeUtils.asElement(returnDeclaredType);
            List<? extends TypeMirror> returnTypeArguments = returnDeclaredType.getTypeArguments();

            if (returnTypeArguments == null || returnTypeArguments.isEmpty()) {
                CodeBlock.Builder returnBuilder;
                if (type.equals(InstanceType.QUERY)) {
                    returnBuilder = CodeBlock.builder()
                            .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L) {\n",
                                    ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(returnTypeElement)),
                                    templateName,
                                    instanceName,
                                    ClassName.get(Transactional.TxType.class),
                                    txType.name(),
                                    rollbackOnArray,
                                    dontRollbackOnArray
                            )
                            .indent()
                            .add("@$T\n", ClassName.get(Override.class))
                            .add("protected $T map($T<String, Object> result) {\n", ClassName.get(returnTypeElement), ClassName.get(Map.class))
                            .indent();

                    entityBuilderCodeBlockList(returnTypeElement).forEach(returnBuilder::add);

                    returnBuilder
                            .unindent()
                            .add("}\n")
                            .unindent()
                            .add("}.query();\n");
                } else {
                    returnBuilder = CodeBlock.builder()
                            .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).update();\n",
                                    ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(returnTypeElement)),
                                    templateName,
                                    instanceName,
                                    ClassName.get(Transactional.TxType.class),
                                    txType.name(),
                                    rollbackOnArray,
                                    dontRollbackOnArray
                            );
                }
                builder.addCode(returnBuilder.build());
            } else {
                TypeMirror returnTypeArgumentTypeMirror = returnTypeArguments.get(0);
                TypeElement returnTypeArgumentTypeElement = (TypeElement) typeUtils.asElement(returnTypeArgumentTypeMirror);
                if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Map.class.getCanonicalName()).asType())) {
                    CodeBlock.Builder returnBuilder = CodeBlock.builder()
                            .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).query();\n",
                                    ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(returnTypeElement)),
                                    templateName,
                                    instanceName,
                                    ClassName.get(Transactional.TxType.class),
                                    txType.name(),
                                    rollbackOnArray,
                                    dontRollbackOnArray
                            );
                    builder.addCode(returnBuilder.build());
                } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(List.class.getCanonicalName()).asType())) {
                    if (typeUtils.isAssignable(returnTypeArgumentTypeElement.asType(), elementUtils.getTypeElement(Map.class.getCanonicalName()).asType())) {
                        CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).queryList();\n",
                                        ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                        templateName,
                                        instanceName,
                                        ClassName.get(Transactional.TxType.class),
                                        txType.name(),
                                        rollbackOnArray,
                                        dontRollbackOnArray
                                );
                        builder.addCode(returnBuilder.build());
                    } else {
                        CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L) {\n",
                                        ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                        templateName,
                                        instanceName,
                                        ClassName.get(Transactional.TxType.class),
                                        txType.name(),
                                        rollbackOnArray,
                                        dontRollbackOnArray
                                )
                                .indent()
                                .add("@$T\n", ClassName.get(Override.class))
                                .add("protected $T map($T<String, Object> result) {\n", ClassName.get(returnTypeArgumentTypeElement), ClassName.get(Map.class))
                                .indent();

                        entityBuilderCodeBlockList(returnTypeArgumentTypeElement).forEach(returnBuilder::add);

                        returnBuilder
                                .unindent()
                                .add("}\n")
                                .unindent()
                                .add("}.queryList();\n");
                        builder.addCode(returnBuilder.build());
                    }
                } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Mono.class.getCanonicalName()).asType())) {
                    if (typeUtils.isAssignable(returnTypeArgumentTypeElement.asType(), elementUtils.getTypeElement(Map.class.getCanonicalName()).asType())) {
                        CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).query();\n",
                                        ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                        templateName,
                                        instanceName,
                                        ClassName.get(Transactional.TxType.class),
                                        txType.name(),
                                        rollbackOnArray,
                                        dontRollbackOnArray
                                );
                        builder.addCode(returnBuilder.build());
                    } else if (typeUtils.isAssignable(returnTypeArgumentTypeElement.asType(), elementUtils.getTypeElement(List.class.getCanonicalName()).asType())) {
                        List<? extends TypeMirror> returnTypeArgumentArguments = ((DeclaredType) returnTypeArgumentTypeMirror).getTypeArguments();
                        TypeMirror returnTypeArgumentArgumentTypeMirror = returnTypeArgumentArguments.get(0);
                        TypeElement returnTypeArgumentArgumentTypeElement = (TypeElement) typeUtils.asElement(returnTypeArgumentArgumentTypeMirror);

                        if (typeUtils.isAssignable(returnTypeArgumentArgumentTypeElement.asType(), elementUtils.getTypeElement(Map.class.getCanonicalName()).asType())) {
                            CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                    .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).queryList();\n",
                                            ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentArgumentTypeElement)),
                                            templateName,
                                            instanceName,
                                            ClassName.get(Transactional.TxType.class),
                                            txType.name(),
                                            rollbackOnArray,
                                            dontRollbackOnArray
                                    );
                            builder.addCode(returnBuilder.build());
                        } else {
                            CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                    .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L) {\n",
                                            ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentArgumentTypeElement)),
                                            templateName,
                                            instanceName,
                                            ClassName.get(Transactional.TxType.class),
                                            txType.name(),
                                            rollbackOnArray,
                                            dontRollbackOnArray
                                    )
                                    .indent()
                                    .add("@$T\n", ClassName.get(Override.class))
                                    .add("protected $T map($T<String, Object> result) {\n", ClassName.get(returnTypeArgumentArgumentTypeElement), ClassName.get(Map.class))
                                    .indent();

                            entityBuilderCodeBlockList(returnTypeArgumentArgumentTypeElement).forEach(returnBuilder::add);

                            returnBuilder
                                    .unindent()
                                    .add("}\n")
                                    .unindent()
                                    .add("}.queryList();\n");
                            builder.addCode(returnBuilder.build());
                        }
                    } else {
                        CodeBlock.Builder returnBuilder;
                        if (type.equals(InstanceType.QUERY)) {
                            returnBuilder = CodeBlock.builder()
                                    .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L) {\n",
                                            ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                            templateName,
                                            instanceName,
                                            ClassName.get(Transactional.TxType.class),
                                            txType.name(),
                                            rollbackOnArray,
                                            dontRollbackOnArray
                                    )
                                    .indent()
                                    .add("@$T\n", ClassName.get(Override.class))
                                    .add("protected $T map($T<String, Object> result) {\n", ClassName.get(returnTypeArgumentTypeElement), ClassName.get(Map.class))
                                    .indent();

                            entityBuilderCodeBlockList(returnTypeArgumentTypeElement).forEach(returnBuilder::add);

                            returnBuilder
                                    .unindent()
                                    .add("}\n")
                                    .unindent()
                                    .add("}.query();\n");
                        } else {
                            returnBuilder = CodeBlock.builder()
                                    .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).update();\n",
                                            ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                            templateName,
                                            instanceName,
                                            ClassName.get(Transactional.TxType.class),
                                            txType.name(),
                                            rollbackOnArray,
                                            dontRollbackOnArray
                                    );
                        }
                        builder.addCode(returnBuilder.build());
                    }
                } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Flux.class.getCanonicalName()).asType())) {
                    if (typeUtils.isAssignable(returnTypeArgumentTypeElement.asType(), elementUtils.getTypeElement(Map.class.getCanonicalName()).asType())) {
                        CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L).queryFlux();\n",
                                        ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                        templateName,
                                        instanceName,
                                        ClassName.get(Transactional.TxType.class),
                                        txType.name(),
                                        rollbackOnArray,
                                        dontRollbackOnArray
                                );
                        builder.addCode(returnBuilder.build());
                    } else {
                        CodeBlock.Builder returnBuilder = CodeBlock.builder()
                                .add("return new $T($S, $S, params, $T.$L, new Class<?>[]$L, new Class<?>[]$L) {\n",
                                        ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(returnTypeArgumentTypeElement)),
                                        templateName,
                                        instanceName,
                                        ClassName.get(Transactional.TxType.class),
                                        txType.name(),
                                        rollbackOnArray,
                                        dontRollbackOnArray
                                )
                                .indent()
                                .add("@$T\n", ClassName.get(Override.class))
                                .add("protected $T map($T<String, Object> result) {\n", ClassName.get(returnTypeArgumentTypeElement), ClassName.get(Map.class))
                                .indent();

                        entityBuilderCodeBlockList(returnTypeArgumentTypeElement).forEach(returnBuilder::add);

                        returnBuilder
                                .unindent()
                                .add("}\n")
                                .unindent()
                                .add("}.queryFlux();\n");
                        builder.addCode(returnBuilder.build());
                    }
                }
            }
        }
        return builder.build();
    }

    private Stream<CodeBlock> entityBuilderCodeBlockList(Element element) {
        if (element.asType().getKind().isPrimitive() ||
                typeUtils.isAssignable(element.asType(), elementUtils.getTypeElement(Boolean.class.getCanonicalName()).asType()) ||
                typeUtils.isAssignable(element.asType(), elementUtils.getTypeElement(Character.class.getCanonicalName()).asType()) ||
                typeUtils.isAssignable(element.asType(), elementUtils.getTypeElement(Number.class.getCanonicalName()).asType()) ||
                typeUtils.isAssignable(element.asType(), elementUtils.getTypeElement(String.class.getCanonicalName()).asType())) {
            return Stream.of(
                    CodeBlock.of("$T<?> iterator = result.values().iterator();\n", ClassName.get(Iterator.class)),
                    CodeBlock.of("if (iterator.hasNext()) {\n"),
                    CodeBlock.of("return ($T) iterator.next();\n", TypeName.get(element.asType())),
                    CodeBlock.of("}\n"),
                    CodeBlock.of("return null;\n")
            );
        } else if (typeUtils.isAssignable(element.asType(), elementUtils.getTypeElement(Map.class.getCanonicalName()).asType())) {
            return Stream.of(CodeBlock.of("return result;\n"));
        } else {
            return Stream.concat(
                    Stream.of(CodeBlock.of("$T entity = new $T();\n", ClassName.get((TypeElement) element), ClassName.get((TypeElement) element))),
                    Stream.concat(
                            element.getEnclosedElements().stream()
                                    .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.METHOD))
                                    .map(enclosedElement -> (ExecutableElement) enclosedElement)
                                    .filter(executableElement -> executableElement.getSimpleName().toString().startsWith("set"))
                                    .map(executableElement ->
                                            CodeBlock.of("entity.$L(result.get($S) != null ? ($T) result.get($S) : null);\n",
                                                    executableElement.getSimpleName().toString(),
                                                    getFiledNameBySetterName(executableElement.getSimpleName().toString()),
                                                    TypeName.get(executableElement.getParameters().get(0).asType()),
                                                    getFiledNameBySetterName(executableElement.getSimpleName().toString())
                                            )
                                    ),
                            Stream.of(CodeBlock.of("return entity;\n"))
                    )
            );
        }
    }

    private String getFiledNameBySetterName(String methodName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName.replaceFirst("set", ""));
    }
}
