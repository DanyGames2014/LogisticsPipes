package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.item.ItemStack;

public interface RequireReliableTransport {
    void itemLost(ItemStack item);
    void itemArrived(ItemStack item);
}
