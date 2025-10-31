package me.bottdev.breezeapi.index;

import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.Optional;

public class BreezeIndexSerializer {

    public final JsonMapper jsonMapper;

    public BreezeIndexSerializer() {
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
        jsonMapper.getRegistry().getTypes().forEach(type -> {
            System.out.println(type + ": ");
            type.getRegisteredClasses().forEach(className -> {
                System.out.println(" - " + className);
            });
        });
        return jsonMapper.deserialize(BreezeIndex.class, jsonString);
    }

}
