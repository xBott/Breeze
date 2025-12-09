package me.bottdev.breezeapi.resource.types;

import lombok.Getter;
import me.bottdev.breezeapi.commons.file.FileCommons;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class FileResource implements BinaryResource {

    private final Path path;
    private final String fileName;
    private byte[] data;

    public FileResource(Path path, byte[] data) {
        this.path = path;
        this.data = data;
        this.fileName = path.getFileName().toString();
    }

    public String getExtension() {
        return FileCommons.getExtension(fileName);
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

    public String read() {
        return new String(data, StandardCharsets.UTF_8);
    }

    public void save(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, data);
    }

}
