package me.bottdev.breezepaper.location;

public interface VectorLike {

    double getX();
    double getY();
    double getZ();

    default double getLength() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
    }

}
