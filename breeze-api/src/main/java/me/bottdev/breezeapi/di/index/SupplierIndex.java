package me.bottdev.breezeapi.di.index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
public class SupplierIndex {

    private static final JsonMapper jsonMapper = new JsonMapper();

    public static Optional<SupplierIndex> fromJson(String json) {
        return jsonMapper.deserialize(SupplierIndex.class, json);
    }

    @Getter
    private List<String> paths = new ArrayList<>();


}
