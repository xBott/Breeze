package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.annotations.Factory;
import me.bottdev.breezeprocessor.AbstractIndexProcessor;
import me.bottdev.breezeprocessor.ProcessingRoundManager;
import me.bottdev.breezeapi.index.types.FactoryIndex;
import me.bottdev.breezeprocessor.rounds.ClassIndexRound;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.lang.annotation.Annotation;

@SuppressWarnings("unused")
@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Factory")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class FactoryIndexProcessor extends AbstractIndexProcessor<FactoryIndex> {

    @Override
    protected String getOutputFileName() {
        return "factory-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return Factory.class;
    }

    @Override
    protected FactoryIndex createIndex() {
        return new FactoryIndex();
    }

    @Override
    protected void configureRounds() {

        ProcessingRoundManager roundManager = getRoundManager();
        Messager messager = getContext().getMessager();

        roundManager.add(new ClassIndexRound(
                messager,
                "Factory Index",
                typeElement -> {
                    String classPath = typeElement.getQualifiedName().toString();
                    FactoryIndex index = getIndex();

                    FactoryIndex.Entry entry = FactoryIndex.Entry.builder()
                            .classPath(classPath)
                            .build();

                    index.addEntry(entry);
                }
        ));
    }

}
