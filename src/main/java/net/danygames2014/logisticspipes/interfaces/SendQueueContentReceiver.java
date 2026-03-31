package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface SendQueueContentReceiver {
    void handleSendQueueItemStackList(LinkedList<ItemStack> _allItems);
}
