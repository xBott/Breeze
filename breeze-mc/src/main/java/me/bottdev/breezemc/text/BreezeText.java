package me.bottdev.breezemc.text;

public interface BreezeText<T extends BreezeTextComponent> {

    T deserialize(String string);

    String serialize(T component);

}
