package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.config.autoload.AutoLoadIndex;
import me.bottdev.breezeapi.config.autoload.AutoLoadSerializer;
import me.bottdev.breezeapi.config.autoload.annotations.AutoLoad;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezeprocessor.AbstractBreezeAnnotationProcessor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.config.autoload.annotations.AutoLoad")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeAutoLoadConfigurationProcessor extends AbstractBreezeAnnotationProcessor {

    @Override
    protected String getOutputFileName() {
        return "breeze-autoload-configuration-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return AutoLoad.class;
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

            AutoLoadIndex index = new AutoLoadIndex(
                    collectedClassInfo.stream().map(info -> {
                                AutoLoad autoLoad = (AutoLoad) info.getAnnotation();
                                String classPath = info.getClassName();
                                String filePath = autoLoad.path();
                                AutoLoadSerializer serializer = autoLoad.serializer();
                                return new AutoLoadIndex.Entry(classPath, filePath, serializer);
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
