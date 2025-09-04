package me.bottdev.breezepaper.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@AllArgsConstructor
@Builder
@Getter
public class BreezeLocation {

    public static BreezeLocation fromBukkit(Location location) {
        World world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return new BreezeLocation(world, x, y, z, yaw, pitch);
    }

    private World world;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    public Location getBukkitLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "BreezeLocation{" +
                "world=" + world.getName() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                "}";
    }
}
