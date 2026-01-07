package me.bottdev.breezepaper.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezemc.text.BreezeTextComponent;
import net.kyori.adventure.text.Component;

@Getter
@RequiredArgsConstructor
public class BreezeAdventureComponent implements BreezeTextComponent {

    private final String value;
    private final Component component;

}
