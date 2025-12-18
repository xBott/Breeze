package me.bottdev.breezeapi.resource.types.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.resource.types.FileResource;

@Getter
@RequiredArgsConstructor
public class SingleFileResource implements FileResource {

    private final TempFile tempFile;

}
