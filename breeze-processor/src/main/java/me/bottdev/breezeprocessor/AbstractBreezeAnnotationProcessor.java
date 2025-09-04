package me.bottdev.breezeprocessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBreezeAnnotationProcessor extends AbstractProcessor {

    protected final Set<ClassInfo> classInfoSet = new HashSet<>();

    protected abstract String getOutputFileName();

    protected abstract Class<? extends Annotation> getAnnotationClass();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
            if (element.getKind() == ElementKind.CLASS) {

                TypeElement typeElement = (TypeElement) element;
                String className = typeElement.getQualifiedName().toString();
                Annotation classAnnotations = typeElement.getAnnotation(getAnnotationClass());

                classInfoSet.add(new ClassInfo(className, classAnnotations));

            }
        }

        if (roundEnv.processingOver()) {
            writeFile();
        }

        return true;
    }

    protected void writeFile() {
        try {

            String name = getOutputFileName();
            PrintWriter writer = new PrintWriter(
                    processingEnv.getFiler()
                            .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/" + name + ".txt")
                            .openWriter()
            );

            for (ClassInfo classInfo : classInfoSet) {
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
