package me.bottdev.breezepaper.text;

import me.bottdev.breezemc.text.BreezeText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class BreezeAdventureText implements BreezeText<BreezeAdventureComponent> {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public BreezeAdventureComponent deserialize(String value) {
        Component component = MiniMessage.miniMessage().deserialize(value);
        return new BreezeAdventureComponent(value, component);
    }

    @Override
    public String serialize(BreezeAdventureComponent component) {
        return component.getValue();
    }

    public BreezeAdventureComponent fromAdventure(Component component) {
        String value = miniMessage.serialize(component);
        return new BreezeAdventureComponent(value, component);
    }

}
