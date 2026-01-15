package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.BeanScope;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeprocessor.*;
import me.bottdev.breezeapi.index.types.ComponentIndex;
import me.bottdev.breezeprocessor.rounds.ClassIndexRound;
import me.bottdev.breezeprocessor.rounds.StaticProcessingRound;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Component")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ComponentIndexProcessor extends AbstractIndexProcessor<ComponentIndex> {

    private static final ComponentDependencyResolver dependencyResolver = new ComponentDependencyResolver();
    private final ComponentDependencyContainer dependencyContainer = new ComponentDependencyContainer();

    @Override
    protected String getOutputFileName() {
        return "component-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return Component.class;
    }

    @Override
    protected ComponentIndex createIndex() {
        return new ComponentIndex();
    }

    @Override
    protected void configureRounds() {

        ProcessingRoundManager roundManager = getRoundManager();
        Messager messager = getContext().getMessager();

        roundManager.add(new ClassIndexRound(
                messager,
                "Component Index",
                this::addDependency
        ));

        roundManager.add(new StaticProcessingRound(
                messager,
                "Dependency resolvation",
                this::resolveDependencies
        ));

    }

    private void addDependency(TypeElement typeElement) {

        Messager messager = getContext().getMessager();

        String classPath = typeElement.getQualifiedName().toString();
        Component annotation = (Component) typeElement.getAnnotation(getAnnotationType());
        BeanScope scope = annotation.type();

        List<VariableElement> dependencies = getInjectedDependencies(typeElement);
        List<String> dependencyPaths = dependencies.stream().map(dependency ->
                dependency.asType().toString()
        ).toList();

        ComponentIndex.Entry entry = ComponentIndex.Entry.builder()
                .classPath(classPath)
                .scope(scope)
                .build();

        ComponentDependent dependent = new ComponentDependent(entry);
        dependent.addDependencies(dependencyPaths);

        dependencyPaths.forEach(dependencyPath ->
                messager.printMessage(Diagnostic.Kind.NOTE, "  Depends on: " + dependencyPath)
        );

        dependencyContainer.add(dependent);

    }

    private void resolveDependencies() {

        Messager messager = getContext().getMessager();

        messager.printMessage(Diagnostic.Kind.NOTE, "Resolving component dependencies...");
        List<ComponentDependent> resolved = dependencyResolver.resolve(dependencyContainer);

        messager.printMessage(Diagnostic.Kind.NOTE, "Successfully resolved component dependencies!");
        messager.printMessage(Diagnostic.Kind.NOTE, "Order of components is:");

        ComponentIndex index = getIndex();
        resolved.forEach(dependent -> {
            messager.printMessage(Diagnostic.Kind.NOTE, "- " + dependent.getDependentId());
            index.addEntry(dependent.getEntry());
        });
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

                    TypeMirror typeMirror = param.asType();
                    Element fieldTypeElement = getContext().getTypeUtils().asElement(typeMirror);

                    if (fieldTypeElement != null && fieldTypeElement.getAnnotation(Component.class) != null) {
                        injectedParams.add(param);
                    }

                }

            }
        }

        return injectedParams;
    }

}
