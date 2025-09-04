package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezeprocessor.AbstractBreezeAnnotationProcessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Component")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class BreezeComponentProcessor extends AbstractBreezeAnnotationProcessor {

    @Override
    protected String getOutputFileName() {
        return "breeze-component-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Component.class;
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

            ComponentIndex index = new ComponentIndex(
                    classInfoSet.stream().map(info -> {
                                Component component = (Component) info.getAnnotation();
                                String classPath = info.getClassName();
                                SupplyType supplyType = component.type();
                                return new ComponentIndex.Entry(classPath, supplyType);
                            }
                    ).toList()
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
