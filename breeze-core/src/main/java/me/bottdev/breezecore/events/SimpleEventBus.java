package me.bottdev.breezecore.events;

import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.events.*;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;

public class SimpleEventBus extends EventBus {

    @Inject
    public SimpleEventBus(BreezeLoggerFactory loggerFactory) {
        super(loggerFactory.simple("SimpleEventBus"));
    }

    @Override
    protected void onStart() {}

}
