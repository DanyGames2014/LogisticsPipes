package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.util.ItemMessage;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface RequestLog {
    void handleMissingItems(LinkedList<ItemMessage> list);
    void handleSucessfullRequestOf(ItemMessage item);
    void handleSucessfullRequestOfList(LinkedList<ItemMessage> items);
}
