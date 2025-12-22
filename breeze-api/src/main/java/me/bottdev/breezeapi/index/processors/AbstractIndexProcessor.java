package me.bottdev.breezeapi.index.processors;

import lombok.Getter;
import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.BreezeIndexRegistry;
import me.bottdev.breezeapi.index.BreezeIndexSerializer;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class AbstractIndexProcessor<T extends BreezeIndex> extends AbstractProcessor {

    @Getter
    private final BreezeLogger logger = new SimpleTreeLogger(this.getClass().getSimpleName());
    private final BreezeIndexSerializer serializer = new BreezeIndexRegistry().getSerializer();
    @Getter
    private T index;
    @Getter
    private Types typeUtils;
    @Getter
    private Messager messager;


    protected abstract String getOutputFileName();
    protected abstract Class<? extends Annotation> getAnnotationType();

    protected abstract T createIndex();
    protected abstract void processElement(Element element);

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        index = createIndex();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(getAnnotationType());
        int processedElements = 0;

        messager.printMessage(Diagnostic.Kind.NOTE,"[Index] Starting element processing:");

        for (Element element : elements) {

            messager.printMessage(Diagnostic.Kind.NOTE, "- Processing element: " + element.toString());

            try {
                processElement(element);
                messager.printMessage(Diagnostic.Kind.NOTE,"  Successfully processed element!");

            } catch (Exception ex) {
                messager.printMessage(Diagnostic.Kind.NOTE,"  Element processing failed: " + ex.getMessage());
            }

            processedElements++;

        }

        messager.printMessage(Diagnostic.Kind.NOTE,"[Index] Finished element processing (" + processedElements + "x elements)");

        if (roundEnv.processingOver()) {
            writeFile(index);
        }

        return false;
    }

    private void writeFile(T index) {
        Filer filer = processingEnv.getFiler();
        try {

            FileObject file = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    "META-INF/" + getOutputFileName() + ".json"
            );

            try (Writer writer = file.openWriter()) {
                String jsonString = serializer.serialize(index);
                writer.write(jsonString);
            }

            messager.printMessage(Diagnostic.Kind.NOTE,"Successfully wrote index to file: " + getOutputFileName() + ".json");

        } catch (IOException ex) {
            messager.printMessage(Diagnostic.Kind.NOTE,"Failed to write index to file: " + ex.getMessage());
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
