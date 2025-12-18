package me.bottdev.breezeapi.commons.file.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface StringReader<T> {

    InputStream getInputStream(T target) throws IOException;

    default void readString(T target, ReadLineConsumer onRead) throws IOException {

        try (InputStream inputStream = getInputStream(target)) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                onRead.accept(line);
            }

        }

    }

    default String readString(T target) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        readString(target, line -> {
            stringBuilder.append(line).append("\n");
        });

        return stringBuilder.toString();

    }

}
