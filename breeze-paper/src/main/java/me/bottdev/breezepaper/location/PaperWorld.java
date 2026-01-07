package me.bottdev.breezepaper.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezemc.world.BreezeLocation;
import me.bottdev.breezemc.world.BreezeWorld;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@RequiredArgsConstructor
public class PaperWorld implements BreezeWorld {

    public static PaperWorld fromPaper(World world) {
        return new PaperWorld(world);
    }

    private final World world;

    @Override
    public String getName() {
        return world.getName();
    }
}
