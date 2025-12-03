package me.bottdev.breezeapi.resource;

public interface BinaryResource extends Resource {

    byte[] toBytes();

    void fromBytes(byte[] data);

}
