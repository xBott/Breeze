package me.bottdev.breezeapi.resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResourceChunkContainer {

    private final List<ResourceChunk> chunks = new ArrayList<>();

    public boolean isEmpty() {
        return chunks.isEmpty();
    }

    public void addChunk(ResourceChunk chunk) {
        chunks.add(chunk);
    }

    public void addChunks(List<ResourceChunk> chunks) {
        this.chunks.addAll(chunks);
    }

    public ResourceChunk getFirst() {
        return chunks.getFirst();
    }

}
