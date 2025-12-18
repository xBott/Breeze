package me.bottdev.breezeapi.commons.file.input;

import java.io.IOException;

@FunctionalInterface
public interface ReadChunkConsumer {

    void accept(byte[] data, int length) throws IOException;

}
