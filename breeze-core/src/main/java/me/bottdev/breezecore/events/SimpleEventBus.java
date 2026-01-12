package me.bottdev.breezecore.events;

import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.events.*;

public class SimpleEventBus extends EventBus {

    @Inject
    public SimpleEventBus(TreeLogger mainLogger) {
        super(mainLogger);
    }

    @Override
    protected void onStart() {}

}
