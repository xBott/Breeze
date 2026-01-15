package me.bottdev.breezeapi.index;

import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.Optional;

public class IndexSerializer {

    public final JsonMapper jsonMapper;

    public IndexSerializer() {
        this.jsonMapper = new JsonMapper();
    }

    public void registerIndex(Class<? extends BreezeIndex> indexClass) {
        jsonMapper.getRegistry()
                .getType(BreezeIndex.class)
                .registerSubtype(indexClass);
    }

    public String serialize(BreezeIndex index) {
        return jsonMapper.serialize(index);
    }

    public Optional<BreezeIndex> deserialize(String jsonString) {
        return jsonMapper.deserialize(BreezeIndex.class, jsonString);
    }

}
