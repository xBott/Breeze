package me.bottdev.breezecore.staged.stages.shutdown;

import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;

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
