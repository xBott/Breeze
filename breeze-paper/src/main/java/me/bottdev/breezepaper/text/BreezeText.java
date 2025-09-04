package me.bottdev.breezepaper.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class BreezeText {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component format(String value) {
        return miniMessage.deserialize(value);
    }

    public static String unformat(Component component) {
        return miniMessage.serialize(component);
    }

}
