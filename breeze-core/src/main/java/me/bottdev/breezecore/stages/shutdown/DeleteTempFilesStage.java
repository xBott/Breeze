package me.bottdev.breezecore.stages.shutdown;

import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezecore.StagedBreezeEngine;

public class DeleteTempFilesStage implements ProcessStage {

    @Override
    public String getName() {
        return "Temp File Deletion";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        TempFiles.cleanup();
    }

}
