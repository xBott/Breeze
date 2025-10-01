package me.bottdev.breezecore.di;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.di.SupplyType;

@Getter
@Setter
@AllArgsConstructor
public class ResolvedDependency {

    private String classPath;
    private SupplyType supplyType;

}
