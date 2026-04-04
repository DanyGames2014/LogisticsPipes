package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface ModuleInventoryReceive {
    void handleInvContent(LinkedList<ItemIdentifierStack> list);
}
