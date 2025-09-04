package me.bottdev.breezeapi.di.index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
public class ComponentIndex {

    private static final JsonMapper jsonMapper = new JsonMapper();

    public static Optional<ComponentIndex> fromJson(String json) {
        return jsonMapper.deserialize(ComponentIndex.class, json);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Entry {
        private String classPath;
        private SupplyType supplyType;
    }

    @Getter
    private List<Entry> entries = new ArrayList<>();

}
