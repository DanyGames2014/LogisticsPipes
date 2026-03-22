package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.minecraft.item.ItemStack;

public interface SendRoutedItem {
    long getSourceId();
    void sendStack(ItemStack stack);
    void sendStack(ItemStack stack, long destination);
    void sendStack(ItemStack stack, long destination, ItemSendMode mode);
}
