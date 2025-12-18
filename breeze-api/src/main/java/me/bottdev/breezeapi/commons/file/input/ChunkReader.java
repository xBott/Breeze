package me.bottdev.breezeapi.commons.file.input;

import java.io.*;

public interface ChunkReader<T> {

    InputStream getInputStream(T target) throws IOException;

    default void readChunks(T target, ReadChunkConsumer onRead) throws IOException {

        try (InputStream inputStream = getInputStream(target)) {

            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                onRead.accept(buffer, read);
            }

        }

    }

    default byte[] readChunks(T target) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        readChunks(target, (buffer, read) -> {
            out.write(buffer, 0, read);
        });

        return out.toByteArray();
    }


}
