package me.bottdev.breezeapi.commons.file.output;

import java.io.BufferedWriter;
import java.io.IOException;

@FunctionalInterface
public interface BufferedWriterConsumer {

    void accept(BufferedWriter writer) throws IOException;

}
