package me.bottdev.breezeadmin;

import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.annotations.Listen;

@Component
public class TestListener implements Listener {

    @Listen(priority = 100)
    public void onTestEvent(TestEvent event) {
        String value = event.getValue();
        System.out.println(value);
    }

}
