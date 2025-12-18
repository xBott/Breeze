package me.bottdev.breezeapi.commons.file.input;

import java.io.IOException;

@FunctionalInterface
public interface ReadLineConsumer {

    void accept(String line) throws IOException;

}
