package me.bottdev.breezeprocessor.writers;

import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.IndexSerializer;
import me.bottdev.breezeprocessor.IndexWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

public class MetaInfIndexWriter<T extends BreezeIndex> implements IndexWriter<T> {
    
    private final Filer filer;
    private final Messager messager;
    private final IndexSerializer serializer;
    
    public MetaInfIndexWriter(Filer filer, Messager messager, IndexSerializer serializer) {
        this.filer = filer;
        this.messager = messager;
        this.serializer = serializer;
    }

    @Override
    public void write(T index, String fileName) {
        try {
            FileObject file = filer.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "META-INF/" + fileName + ".json"
            );
            
            try (Writer writer = file.openWriter()) {
                String jsonString = serializer.serialize(index);
                writer.write(jsonString);
            }
            
            messager.printMessage(Diagnostic.Kind.NOTE, 
                "Successfully wrote index to file: " + fileName + ".json");
                
        } catch (IOException ex) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                "Failed to write index to file: " + ex.getMessage());
        }
    }

}