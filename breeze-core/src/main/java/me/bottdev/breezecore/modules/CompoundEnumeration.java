package me.bottdev.breezecore.modules;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CompoundEnumeration<E> implements Enumeration<E> {

    private final Enumeration<E>[] enums;
    private int index = 0;

    public CompoundEnumeration(Enumeration<E>[] enums) {
        this.enums = enums;
    }

    @Override
    public boolean hasMoreElements() {
        while (index < enums.length) {
            if (enums[index].hasMoreElements()) return true;
            index++;
        }
        return false;
    }

    @Override
    public E nextElement() {
        if (!hasMoreElements()) {
            throw new NoSuchElementException();
        }
        return enums[index].nextElement();
    }
}
