package me.bottdev.breezeapi.commons.file.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BreezeFileReader implements LineReader<File>, ChunkReader<File> {

    public static final BreezeFileReader INSTANCE = new BreezeFileReader();

    @Override
    public InputStream getInputStream(File target) throws IOException {
        return new FileInputStream(target);
    }

}
