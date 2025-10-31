package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.index.processors.AbstractIndexProcessor;
import me.bottdev.breezeapi.index.processors.ClassIndexProcessor;
import me.bottdev.breezeapi.index.types.BreezeModuleIndex;
import me.bottdev.breezeapi.modules.annotations.ModuleDependency;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.modules.annotations.ModuleInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeModuleIndexProcessor extends AbstractIndexProcessor<BreezeModuleIndex> implements ClassIndexProcessor {


    @Override
    protected String getOutputFileName() {
        return "module-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return ModuleInfo.class;
    }

    @Override
    protected BreezeModuleIndex createIndex() {
        return new BreezeModuleIndex();
    }

    @Override
    protected void processElement(Element element) {
        processClass(element);
    }

    @Override
    public void extractDataFromClass(TypeElement typeElement) {
        String classPath = typeElement.getQualifiedName().toString();
        ModuleInfo annotation = (ModuleInfo) typeElement.getAnnotation(getAnnotationType());

        String moduleName = annotation.name();
        String version = annotation.version();
        ModuleDependency[] dependencies = annotation.dependencies();

        List<String> mappedDependencies = Arrays.stream(dependencies)
                .map(ModuleDependency::name)
                .toList();

        BreezeModuleIndex index = getIndex();
        index.setClassPath(classPath);
        index.setModuleName(moduleName);
        index.setVersion(version);
        index.setDependencies(mappedDependencies);

    }

}
