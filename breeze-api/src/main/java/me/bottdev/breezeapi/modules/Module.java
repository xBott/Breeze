package me.bottdev.breezeapi.modules;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public abstract class Module {

    private ModuleStatus status = ModuleStatus.DISABLED;
    private File dataFolder;
    private ModuleDescriptor descriptor;

    public Module(File dataFolder, ModuleDescriptor descriptor) {
        this.dataFolder = dataFolder;
        this.descriptor = descriptor;
    }

    public abstract void onEnable();

    public abstract void onRestart();

    public abstract void onDisable();

}
