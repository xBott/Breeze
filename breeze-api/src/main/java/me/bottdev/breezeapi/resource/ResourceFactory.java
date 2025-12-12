package me.bottdev.breezeapi.resource;

import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.types.TreeFileResource;

import java.nio.file.Path;
import java.util.Optional;

public class ResourceFactory {

    public static <T extends Resource> Optional<T> create(Class<T> clazz, ResourceChunkContainer chunkContainer) {

        if (chunkContainer.isEmpty()) return Optional.empty();

        if (clazz == FileResource.class) {

            ResourceChunk chunk = chunkContainer.getFirst();
            FileResource resource = createFileResource(chunk);
            return Optional.of(clazz.cast(resource));

        } else if (clazz == TreeFileResource.class) {

            TreeFileResource resource = createTreeFileResource(chunkContainer);
            return Optional.of(clazz.cast(resource));

        }

        return Optional.empty();

    }

    private static FileResource createFileResource(ResourceChunk chunk) {
        byte[] data = chunk.getData();
        Optional<Path> path = chunk.getMetadata().get("path");
        return path.map(value -> new FileResource(value, data)).orElse(null);
    }

    private static TreeFileResource createTreeFileResource(ResourceChunkContainer chunkContainer) {

        TreeFileResource resource = new TreeFileResource();

        for (ResourceChunk chunk : chunkContainer.getChunks()) {

            Optional<String> relativePathOptional = chunk.getMetadata().get("relative");
            if (relativePathOptional.isEmpty()) continue;
            String relativePath = relativePathOptional.get();

            FileResource fileResource = createFileResource(chunk);
            if (fileResource == null) continue;

            resource.add(relativePath, fileResource);

        }

        return resource;

    }

}
