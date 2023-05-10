package io.sqltemplate.annotation.processor;

import com.google.auto.service.AutoService;
import io.sqltemplate.spi.annotation.Template;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_8;

@SupportedAnnotationTypes("io.sqltemplate.spi.annotation.Template")
@SupportedSourceVersion(RELEASE_8)
@AutoService(Processor.class)
public class TemplateProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        TemplateInterfaceBuilder templateInterfaceBuilder = new TemplateInterfaceBuilder(processingEnv);
        roundEnv.getElementsAnnotatedWith(Template.class).stream()
                .filter(element -> element.getKind().equals(ElementKind.INTERFACE))
                .map(element -> (TypeElement) element)
                .forEach(element -> {
                            try {
                                templateInterfaceBuilder.write(element);
                            } catch (IOException e) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                            }
                        }
                );
        return false;
    }


}
