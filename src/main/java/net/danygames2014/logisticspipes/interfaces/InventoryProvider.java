package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface InventoryProvider {
    Inventory getInventory();

    Direction inventoryDirection();
}
