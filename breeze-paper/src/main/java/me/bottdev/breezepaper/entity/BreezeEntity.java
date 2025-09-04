package me.bottdev.breezepaper.entity;

import me.bottdev.breezepaper.location.BreezeLocation;
import org.bukkit.entity.Entity;

public interface BreezeEntity {

    Entity getBukkitEntity();

    BreezeLocation getLocation();

}
