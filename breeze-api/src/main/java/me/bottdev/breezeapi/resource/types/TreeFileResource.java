package me.bottdev.breezeapi.resource.types;

import lombok.Getter;
import me.bottdev.breezeapi.resource.Resource;

import java.util.HashMap;
import java.util.Optional;

@Getter
public class TreeFileResource implements Resource {

    private final HashMap<String, FileResource> data = new HashMap<>();

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public TreeFileResource add(String key, FileResource resource) {
        data.put(key, resource);
        return this;
    }

    public Optional<FileResource> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

}
