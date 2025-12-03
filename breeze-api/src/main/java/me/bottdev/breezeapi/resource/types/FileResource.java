package me.bottdev.breezeapi.resource.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.FileCommons;
import me.bottdev.breezeapi.resource.BinaryResource;
import me.bottdev.breezeapi.resource.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class FileResource implements Resource, BinaryResource {

    private final String fileName;
    private byte[] data;

    public String getExtension() {
        return FileCommons.getExtension(fileName);
    }

    @Override
    public byte[] toBytes() {
        return data;
    }

    @Override
    public void fromBytes(byte[] data) {
        this.data = data;
    }

    public String read() {
        return new String(data, StandardCharsets.UTF_8);
    }

    public void save(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, data);
    }

}
