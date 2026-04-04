package net.danygames2014.logisticspipes.request;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface RequestLog {
    void handleMissingItems(LinkedList<ItemStack> list);
    void handleSucessfullRequestOf(ItemStack item);
    void handleSucessfullRequestOfList(LinkedList<ItemStack> items);
}
