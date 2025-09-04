package me.bottdev.breezeapi.modules;

public interface Module {

    ModuleStatus getStatus();

    void setStatus(ModuleStatus status);

    void onEnable();

    void onRestart();

    void onDisable();

}
