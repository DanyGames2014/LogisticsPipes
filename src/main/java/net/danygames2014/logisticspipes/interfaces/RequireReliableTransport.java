package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.minecraft.item.ItemStack;

public interface RequireReliableTransport {
    void itemLost(ItemIdentifier item);
    void itemArrived(ItemIdentifier item);
}
