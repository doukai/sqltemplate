import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.Param;
import io.sqltemplate.spi.annotation.Template;
import io.sqltemplate.spi.annotation.TemplateType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.SourceVersion.RELEASE_8;

@SupportedAnnotationTypes("io.sqltemplate.spi.annotation.Template")
@SupportedSourceVersion(RELEASE_8)
@AutoService(Processor.class)
public class TemplateProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        roundEnv.getElementsAnnotatedWith(Template.class).stream()
                .filter(element -> element.getKind().equals(ElementKind.INTERFACE))
                .map(element -> (TypeElement) element)
                .map(this::buildTemplateInterface)
                .forEach(javaFile -> {
                            try {
                                javaFile.writeTo(filer);
                            } catch (IOException e) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                            }
                        }
                );
        return false;
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
        TemplateType type = templateAnnotation.type();

        String instanceName = executableElement.getSimpleName().toString();
        Instance instanceAnnotation = executableElement.getAnnotation(Instance.class);
        if (instanceAnnotation != null) {
            instanceName = instanceAnnotation.value();
        }

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

        DeclaredType returnType = (DeclaredType) executableElement.getReturnType();
        TypeElement returnTypeElement = (TypeElement) typeUtils.asElement(returnType);
        List<? extends TypeMirror> typeArguments = returnType.getTypeArguments();

        if (typeArguments == null || typeArguments.isEmpty()) {
            CodeBlock.Builder returnBuilder = CodeBlock.builder()
                    .add("return new $T($S, $T.$L, $S, params) {\n",
                            ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(returnTypeElement)),
                            templateName,
                            ClassName.get(TemplateType.class),
                            type.name(),
                            instanceName
                    )
                    .indent()
                    .add("@$T\n", ClassName.get(Override.class))
                    .add("protected $T map($T<String, Object> result) {\n", ClassName.get(returnTypeElement), ClassName.get(Map.class))
                    .indent()
                    .add("$T entity = new $T();\n", ClassName.get(returnTypeElement), ClassName.get(returnTypeElement));

            entityBuilderCodeBlockList(returnTypeElement).forEach(returnBuilder::add);

            returnBuilder
                    .add("return entity;\n", ClassName.get(returnTypeElement), ClassName.get(returnTypeElement))
                    .unindent()
                    .add("}\n")
                    .unindent()
                    .add("}.query();\n");
            builder.addCode(returnBuilder.build())
                    .addException(ClassName.get(SQLException.class));
        } else {
            TypeMirror argumentTypeMirror = typeArguments.get(0);
            TypeElement argumentTypeElement = (TypeElement) typeUtils.asElement(argumentTypeMirror);
            if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(List.class.getCanonicalName()).asType())) {
                CodeBlock.Builder returnBuilder = CodeBlock.builder()
                        .add("return new $T($S, $T.$L, $S, params) {\n",
                                ParameterizedTypeName.get(ClassName.get(JDBCAdapter.class), ClassName.get(argumentTypeElement)),
                                templateName,
                                ClassName.get(TemplateType.class),
                                type.name(),
                                instanceName
                        )
                        .indent()
                        .add("@$T\n", ClassName.get(Override.class))
                        .add("protected $T map($T<String, Object> result) {\n", ClassName.get(argumentTypeElement), ClassName.get(Map.class))
                        .indent()
                        .add("$T entity = new $T();\n", ClassName.get(argumentTypeElement), ClassName.get(argumentTypeElement));

                entityBuilderCodeBlockList(argumentTypeElement).forEach(returnBuilder::add);

                returnBuilder
                        .add("return entity;\n", ClassName.get(argumentTypeElement), ClassName.get(argumentTypeElement))
                        .unindent()
                        .add("}\n")
                        .unindent()
                        .add("}.queryList();\n");
                builder.addCode(returnBuilder.build())
                        .addException(ClassName.get(SQLException.class));
            } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Mono.class.getCanonicalName()).asType())) {
                if (typeUtils.isAssignable(argumentTypeElement.asType(), elementUtils.getTypeElement(List.class.getCanonicalName()).asType())) {
                    List<? extends TypeMirror> argumentTypeArguments = ((DeclaredType) argumentTypeMirror).getTypeArguments();
                    TypeMirror argumentTypeArgumentTypeMirror = argumentTypeArguments.get(0);
                    TypeElement argumentTypeArgumentTypeElement = (TypeElement) typeUtils.asElement(argumentTypeArgumentTypeMirror);
                    CodeBlock.Builder returnBuilder = CodeBlock.builder()
                            .add("return new $T($S, $T.$L, $S, params) {\n",
                                    ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(argumentTypeArgumentTypeElement)),
                                    templateName,
                                    ClassName.get(TemplateType.class),
                                    type.name(),
                                    instanceName
                            )
                            .indent()
                            .add("@$T\n", ClassName.get(Override.class))
                            .add("protected $T map($T<String, Object> result) {\n", ClassName.get(argumentTypeArgumentTypeElement), ClassName.get(Map.class))
                            .indent()
                            .add("$T entity = new $T();\n", ClassName.get(argumentTypeArgumentTypeElement), ClassName.get(argumentTypeArgumentTypeElement));

                    entityBuilderCodeBlockList(argumentTypeArgumentTypeElement).forEach(returnBuilder::add);

                    returnBuilder
                            .add("return entity;\n", ClassName.get(argumentTypeArgumentTypeElement), ClassName.get(argumentTypeArgumentTypeElement))
                            .unindent()
                            .add("}\n")
                            .unindent()
                            .add("}.queryList();\n");
                    builder.addCode(returnBuilder.build());
                } else {
                    CodeBlock.Builder returnBuilder = CodeBlock.builder()
                            .add("return new $T($S, $T.$L, $S, params) {\n",
                                    ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(argumentTypeElement)),
                                    templateName,
                                    ClassName.get(TemplateType.class),
                                    type.name(),
                                    instanceName
                            )
                            .indent()
                            .add("@$T\n", ClassName.get(Override.class))
                            .add("protected $T map($T<String, Object> result) {\n", ClassName.get(argumentTypeElement), ClassName.get(Map.class))
                            .indent()
                            .add("$T entity = new $T();\n", ClassName.get(argumentTypeElement), ClassName.get(argumentTypeElement));

                    entityBuilderCodeBlockList(argumentTypeElement).forEach(returnBuilder::add);

                    returnBuilder
                            .add("return entity;\n", ClassName.get(argumentTypeElement), ClassName.get(argumentTypeElement))
                            .unindent()
                            .add("}\n")
                            .unindent()
                            .add("}.query();\n");
                    builder.addCode(returnBuilder.build());
                }
            } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Flux.class.getCanonicalName()).asType())) {
                CodeBlock.Builder returnBuilder = CodeBlock.builder()
                        .add("return new $T($S, $T.$L, $S, params) {\n",
                                ParameterizedTypeName.get(ClassName.get(R2DBCAdapter.class), ClassName.get(argumentTypeElement)),
                                templateName,
                                ClassName.get(TemplateType.class),
                                type.name(),
                                instanceName
                        )
                        .indent()
                        .add("@$T\n", ClassName.get(Override.class))
                        .add("protected $T map($T<String, Object> result) {\n", ClassName.get(argumentTypeElement), ClassName.get(Map.class))
                        .indent()
                        .add("$T entity = new $T();\n", ClassName.get(argumentTypeElement), ClassName.get(argumentTypeElement));

                entityBuilderCodeBlockList(argumentTypeElement).forEach(returnBuilder::add);

                returnBuilder
                        .add("return entity;\n", ClassName.get(argumentTypeElement), ClassName.get(argumentTypeElement))
                        .unindent()
                        .add("}\n")
                        .unindent()
                        .add("}.queryFlux();\n");
                builder.addCode(returnBuilder.build());
            }
        }
        return builder.build();
    }

    private List<CodeBlock> entityBuilderCodeBlockList(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream().filter(element -> element.getKind().equals(ElementKind.METHOD))
                .map(element -> (ExecutableElement) element)
                .filter(executableElement -> executableElement.getSimpleName().toString().startsWith("set"))
                .map(executableElement ->
                        CodeBlock.of("entity.$L(result.get($S) != null ? ($T) result.get($S) : null);\n",
                                executableElement.getSimpleName().toString(),
                                getFiledNameBySetterName(executableElement.getSimpleName().toString()),
                                ClassName.get(executableElement.getParameters().get(0).asType()),
                                getFiledNameBySetterName(executableElement.getSimpleName().toString())
                        )
                )
                .collect(Collectors.toList());
    }

    private String getFiledNameBySetterName(String methodName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName.replaceFirst("set", ""));
    }
}
