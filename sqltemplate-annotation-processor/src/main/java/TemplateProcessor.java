import com.google.auto.service.AutoService;
import com.google.common.reflect.TypeToken;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.sqltemplate.spi.annotation.Template;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.SourceVersion.RELEASE_8;

@SupportedAnnotationTypes("io.sqltemplate.spi.Template")
@SupportedSourceVersion(RELEASE_8)
@AutoService(Processor.class)
public class TemplateProcessor extends AbstractProcessor {

    private Filer filer;
    Elements elementUtils;
    Types typeUtils;

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
                .map(this::buildTemplateInterface);


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
        TypeName typeName = ClassName.get(executableElement.getReturnType());
        CodeBlock mapOf = CodeBlock.join(
                executableElement.getParameters().stream()
                        .map(parameter ->
                                CodeBlock.of(
                                        "$S, (Object)$L",
                                        parameter.getSimpleName().toString(),
                                        parameter.getSimpleName().toString()
                                )
                        )
                        .collect(Collectors.toList()),
                ", ");

        MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addParameters(
                        executableElement.getParameters().stream()
                                .map(variableElement -> ParameterSpec.builder(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString()).build())
                                .collect(Collectors.toList())
                )
                .returns(typeName)
                .addException(ClassName.get(Exception.class));

        DeclaredType returnType = (DeclaredType) executableElement.getReturnType();
        List<? extends TypeMirror> typeArguments = returnType.getTypeArguments();
        TypeElement returnTypeElement = (TypeElement) typeUtils.asElement(returnType);
        if (typeArguments == null || typeArguments.isEmpty()) {

        } else {
            TypeMirror typeMirror = typeArguments.get(0);
            TypeElement argumentTypeElement = (TypeElement) typeUtils.asElement(typeMirror);

            if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Collection.class.getCanonicalName()).asType())) {

            } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Mono.class.getCanonicalName()).asType())) {
                if (typeUtils.isAssignable(argumentTypeElement.asType(), elementUtils.getTypeElement(Collection.class.getCanonicalName()).asType())) {

                } else {

                }
            } else if (typeUtils.isAssignable(returnTypeElement.asType(), elementUtils.getTypeElement(Flux.class.getCanonicalName()).asType())) {

            } else {

            }
        }
        return builder.build();
    }

    private boolean isReturnCollection(ExecutableElement executableElement) {
        TypeName typeName0 = ClassName.get(executableElement.getReturnType());
        if (typeName0 instanceof ParameterizedTypeName) {
            if (((ParameterizedTypeName) typeName0).rawType.canonicalName().equals(Mono.class.getCanonicalName())) {
                TypeName typeName1 = ((ParameterizedTypeName) typeName0).typeArguments.get(0);
                return typeName1 instanceof ParameterizedTypeName;
            }
        }
        return false;
    }
}
