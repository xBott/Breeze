package me.bottdev.breezeapi.commons.file.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BreezeFileWriter implements ChunkWriter<File>, StringWriter<File> {

    public static final BreezeFileWriter INSTANCE = new BreezeFileWriter();

    @Override
    public OutputStream getOutputStream(File target) throws IOException {
        return new FileOutputStream(target);
    }

}
