package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface InventoryProvider {
    Inventory getInventory();
    Direction inventoryDirection();
}
