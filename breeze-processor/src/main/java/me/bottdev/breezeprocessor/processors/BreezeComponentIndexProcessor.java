package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;
import me.bottdev.breezeapi.index.processors.AbstractIndexProcessor;
import me.bottdev.breezeapi.index.processors.ClassIndexProcessor;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Component")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeComponentIndexProcessor extends AbstractIndexProcessor<BreezeComponentIndex> implements ClassIndexProcessor {


    @Override
    protected String getOutputFileName() {
        return "component-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return Component.class;
    }

    @Override
    protected BreezeComponentIndex createIndex() {
        return new BreezeComponentIndex();
    }

    @Override
    protected void processElement(Element element) {
        processClass(element);
    }

    @Override
    public void extractDataFromClass(TypeElement typeElement) {
        String classPath = typeElement.getQualifiedName().toString();
        Component annotation = (Component) typeElement.getAnnotation(getAnnotationType());

        BreezeComponentIndex index = getIndex();

        SupplyType supplyType = annotation.type();

        List<VariableElement> dependencies = getInjectedDependencies(typeElement);
        List<String> stringDependencies = dependencies.stream().map(dependency ->
                dependency.asType().toString()
        ).toList();

        BreezeComponentIndex.Entry entry = BreezeComponentIndex.Entry.builder()
                .classPath(classPath)
                .supplyType(supplyType)
                .dependencies(stringDependencies)
                .build();

        index.addEntry(entry);

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
                        Element fieldTypeElement = getTypeUtils().asElement(typeMirror);

                        if (fieldTypeElement != null && fieldTypeElement.getAnnotation(Component.class) != null) {
                            injectedParams.add(param);
                        }

                    }
                }
            }
        }

        return injectedParams;
    }

}
