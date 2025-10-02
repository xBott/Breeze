package me.bottdev.breezeapi.config.autoload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AutoLoadIndex {

    private static final JsonMapper jsonMapper = new JsonMapper();

    public static Optional<AutoLoadIndex> fromJson(String json) {
        return jsonMapper.deserialize(AutoLoadIndex.class, json);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry {
        private String classPath;
        private String filePath;
        private AutoLoadSerializer serializer;
    }

    private List<Entry> entries = new ArrayList<>();

}
