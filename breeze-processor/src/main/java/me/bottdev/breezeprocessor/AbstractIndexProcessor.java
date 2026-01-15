package me.bottdev.breezeprocessor;

import lombok.Getter;
import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.IndexRegistry;
import me.bottdev.breezeapi.index.IndexSerializer;
import me.bottdev.breezeprocessor.writers.MetaInfIndexWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class AbstractIndexProcessor<T extends BreezeIndex> extends AbstractProcessor {

    private final IndexSerializer serializer = new IndexRegistry().getSerializer();

    @Getter
    private T index;
    private IndexWriter<T> indexWriter;
    @Getter
    private ProcessorContext context;
    @Getter
    private ProcessingRoundManager roundManager;

    protected abstract String getOutputFileName();
    protected abstract Class<? extends Annotation> getAnnotationType();
    protected abstract T createIndex();
    protected abstract void configureRounds();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.index = createIndex();

        this.context = new ProcessorContext(
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getMessager(),
                processingEnv.getFiler()
        );

        this.indexWriter = new MetaInfIndexWriter<>(
                context.getFiler(),
                context.getMessager(),
                serializer
        );

        this.roundManager = new ProcessingRoundManager();
        configureRounds();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(getAnnotationType());

        if (!elements.isEmpty()) {
            roundManager.executeRounds(elements);
        }

        if (roundEnv.processingOver()) {
            indexWriter.write(index, getOutputFileName());
        }

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
