package me.bottdev.breezeapi.resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResourceChunk {

    private final byte[] data;
    private final ResourceMetadata metadata;

}
