package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface ModuleInventoryReceive {
    void handleInvContent(LinkedList<ItemStack> list);
}
