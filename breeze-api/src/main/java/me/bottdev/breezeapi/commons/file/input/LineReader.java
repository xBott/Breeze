package me.bottdev.breezeapi.commons.file.input;

import me.bottdev.breezeapi.log.BreezeLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public interface LineReader<T> {

    InputStream getInputStream(T target) throws IOException;

    default void readLines(T target, ReadLineConsumer onRead) throws IOException {

        try (InputStream inputStream = getInputStream(target)) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                onRead.accept(line);
            }

        }

    }

    default String readLines(T target) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        readLines(target, line -> {
            stringBuilder.append(line).append("\n");
        });

        return stringBuilder.toString();

    }

}
