package me.bottdev.breezemc.world;

public interface BreezeLocation extends VectorLike {

    BreezeWorld getWorld();
    float getYaw();
    float getPitch();

}
