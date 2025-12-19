package me.bottdev.breezeapi.resource.types.file;

import lombok.Getter;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Getter
public class ImageResource implements FileResource {

    private final TempFile tempFile;
    private final SourceType sourceType;
    private final BufferedImage image;

    public ImageResource(TempFile tempFile, SourceType sourceType) throws IOException {
        this.tempFile = tempFile;
        this.sourceType = sourceType;
        this.image = ImageIO.read(tempFile.toFile());
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

}
