package me.bottdev.breezeapi.commons.file.output;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface OutputStreamConsumer {

    void accept(OutputStream out) throws IOException;

}
