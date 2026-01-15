package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeprocessor.AbstractIndexProcessor;
import me.bottdev.breezeapi.index.types.ModuleIndex;
import me.bottdev.breezeapi.modules.annotations.ModuleDependency;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;
import me.bottdev.breezeprocessor.ProcessingRoundManager;
import me.bottdev.breezeprocessor.rounds.ClassIndexRound;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.modules.annotations.ModuleInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ModuleIndexProcessor extends AbstractIndexProcessor<ModuleIndex> {


    @Override
    protected String getOutputFileName() {
        return "module-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return ModuleInfo.class;
    }

    @Override
    protected ModuleIndex createIndex() {
        return new ModuleIndex();
    }

    @Override
    protected void configureRounds() {

        ProcessingRoundManager roundManager = getRoundManager();
        Messager messager = getContext().getMessager();

        roundManager.add(new ClassIndexRound(
                messager,
                "Module Index",
                typeElement -> {
                    String classPath = typeElement.getQualifiedName().toString();
                    ModuleInfo annotation = (ModuleInfo) typeElement.getAnnotation(getAnnotationType());

                    String moduleName = annotation.name();
                    String version = annotation.version();
                    ModuleDependency[] dependencies = annotation.dependencies();

                    List<String> mappedDependencies = Arrays.stream(dependencies)
                            .map(ModuleDependency::name)
                            .toList();

                    ModuleIndex index = getIndex();
                    index.setClassPath(classPath);
                    index.setModuleName(moduleName);
                    index.setVersion(version);
                    index.setDependencies(mappedDependencies);
                }
        ));
    }

}
