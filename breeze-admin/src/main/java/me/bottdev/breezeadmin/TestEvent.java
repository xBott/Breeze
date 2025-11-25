package me.bottdev.breezeadmin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.Event;

@Getter
@RequiredArgsConstructor
public class TestEvent implements Event {
    private final String value;
}
