package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.annotations.Supplier;
import me.bottdev.breezeapi.di.index.SupplierIndex;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezeprocessor.AbstractBreezeAnnotationProcessor;
import me.bottdev.breezeprocessor.ClassInfo;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Supplier")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeSupplierProcessor extends AbstractBreezeAnnotationProcessor {

    @Override
    protected String getOutputFileName() {
        return "breeze-supplier-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Supplier.class;
    }

    @Override
    protected void writeFile() {
        try {
            String name = getOutputFileName();
            PrintWriter writer = new PrintWriter(
                    processingEnv.getFiler()
                            .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/" + name + ".json")
                            .openWriter()
            );

            SupplierIndex index = new SupplierIndex(
                    classInfoSet.stream().map(ClassInfo::getClassName).toList()
            );
            JsonMapper mapper = new JsonMapper();
            String jsonString = mapper.serialize(index);

            writer.print(jsonString);
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
