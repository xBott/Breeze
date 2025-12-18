package me.bottdev.breezeapi.commons.file.output;

import java.io.IOException;
import java.io.OutputStream;

public interface ChunkWriter<T> {

    OutputStream getOutputStream(T target) throws IOException;

    default void writeChunks(T target, OutputStreamConsumer consumer) throws IOException {

        try (OutputStream outputStream = getOutputStream(target)) {

            consumer.accept(outputStream);
            outputStream.flush();

        }

    }

}
