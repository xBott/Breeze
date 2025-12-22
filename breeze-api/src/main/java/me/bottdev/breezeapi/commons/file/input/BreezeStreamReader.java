package me.bottdev.breezeapi.commons.file.input;

import java.io.IOException;
import java.io.InputStream;

public class BreezeStreamReader implements StringReader<InputStream>, ChunkReader<InputStream> {

    public static final BreezeStreamReader INSTANCE = new BreezeStreamReader();

    @Override
    public InputStream getInputStream(InputStream target) throws IOException {
        return target;
    }

}
