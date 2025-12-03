package me.bottdev.breezeapi.serialization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MapperType {

    public final Class<? extends Mapper> mapperClass;
    public final String extension;

}
