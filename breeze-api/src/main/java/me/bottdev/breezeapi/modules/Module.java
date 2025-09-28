package me.bottdev.breezeapi.modules;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public abstract class Module {

    private ModuleStatus status;
    private File dataFolder;

    public abstract void onEnable();

    public abstract void onRestart();

    public abstract void onDisable();

}
