package me.bottdev.breezeapi.script.values;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bottdev.breezeapi.script.Value;

@AllArgsConstructor
@Getter
public class IntegerValue implements Value {

    private Integer value;

    @Override
    public String toString() {
        return value.toString();
    }
}
