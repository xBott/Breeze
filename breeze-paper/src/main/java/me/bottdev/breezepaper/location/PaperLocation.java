package me.bottdev.breezepaper.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezemc.world.BreezeLocation;
import me.bottdev.breezemc.world.BreezeWorld;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public class PaperLocation implements BreezeLocation {

    public static PaperLocation fromPaper(Location location) {
        return new PaperLocation(
                new PaperWorld(location.getWorld()),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    private final BreezeWorld world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

}
