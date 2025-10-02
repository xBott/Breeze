package me.bottdev.breezeprocessor;

import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.*;

public abstract class AbstractBreezeAnnotationProcessor extends AbstractProcessor {

    protected final Set<ClassInfo> collectedClassInfo = new HashSet<>();

    protected abstract String getOutputFileName();

    protected abstract Class<? extends Annotation> getAnnotationClass();

    private Types typeUtils;

    public AbstractBreezeAnnotationProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        prepareClassInfoSet(annotations, roundEnv);

        if (roundEnv.processingOver()) {
            writeFile();
        }

        return true;
    }

    protected void prepareClassInfoSet(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
            if (element.getKind() == ElementKind.CLASS) {

                TypeElement typeElement = (TypeElement) element;

                String className = typeElement.getQualifiedName().toString();

                Annotation classAnnotations = typeElement.getAnnotation(getAnnotationClass());

                List<String> dependencies = getInjectedDependencies(typeElement).stream().map(
                        varElement -> varElement.asType().toString()
                ).toList();

                collectedClassInfo.add(new ClassInfo(className, classAnnotations, dependencies));

            }
        }

    }

    protected List<VariableElement> getInjectedDependencies(TypeElement typeElement) {
        List<VariableElement> injectedDependencies = new ArrayList<>();

        injectedDependencies.addAll(getInjectedDependenciesFromFields(typeElement));
        injectedDependencies.addAll(getInjectedDependenciesFromConstructor(typeElement));

        return injectedDependencies;
    }

    protected List<VariableElement> getInjectedDependenciesFromFields(TypeElement typeElement) {
        List<VariableElement> injectedFields = new ArrayList<>();

        for (Element enclosed : typeElement.getEnclosedElements()) {

            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosed;

                if (field.getAnnotation(Inject.class) != null) {
                    injectedFields.add(field);
                }
            }
        }

        return injectedFields;
    }

    protected List<VariableElement> getInjectedDependenciesFromConstructor(TypeElement typeElement) {
        List<VariableElement> injectedParams = new ArrayList<>();

        for (Element enclosed : typeElement.getEnclosedElements()) {

            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement) enclosed;

                for (VariableElement param : constructor.getParameters()) {

                    if (param.getAnnotation(Named.class) != null) {

                        TypeMirror typeMirror = param.asType();
                        Element fieldTypeElement = typeUtils.asElement(typeMirror);

                        if (fieldTypeElement != null && fieldTypeElement.getAnnotation(Component.class) != null) {
                            injectedParams.add(param);
                        }

                    }
                }
            }
        }

        return injectedParams;
    }

    protected void writeFile() {
        try {

            String name = getOutputFileName();
            PrintWriter writer = new PrintWriter(
                    processingEnv.getFiler()
                            .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/" + name + ".txt")
                            .openWriter()
            );

            for (ClassInfo classInfo : collectedClassInfo) {
                writeLine(writer, classInfo);
            }

            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void writeLine(PrintWriter writer, ClassInfo classInfo) {
        writer.println(classInfo.getClassName());
    }

}
