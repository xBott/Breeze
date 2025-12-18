package me.bottdev.breezepaper.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class BreezeInventory implements InventoryHolder {

    private final Inventory inventory;

    public BreezeInventory() {
        this.inventory = Bukkit.createInventory(null, 9, "Breeze Inventory");
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}
