package me.bottdev.breezeapi.di;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class BindingKey<T> {

    public static <T> BindingKey<T> of(Class<T> type, String qualifier) {
        return new BindingKey<T>(type, qualifier);
    }

    private final Class<T> type;
    private final String qualifier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BindingKey<?> other)) return false;

        return type.equals(other.type)
                && Objects.equals(qualifier, other.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, qualifier);
    }

    @Override
    public String toString() {
        return qualifier == null
                ? type.getName()
                : type.getName() + "@" + qualifier;
    }

}
