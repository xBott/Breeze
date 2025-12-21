package me.bottdev.breezeapi.commons.file.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public interface StringWriter<T> {

    OutputStream getOutputStream(T target) throws IOException;

    default void writeString(T target, BufferedWriterConsumer consumer) throws IOException {

        try (OutputStream outputStream = getOutputStream(target);
             BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
             )) {

            consumer.accept(bufferedWriter);
        }
    }


}
