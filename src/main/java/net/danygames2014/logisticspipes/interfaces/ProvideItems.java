package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface ProvideItems {
    void canProvide(RequestTreeNode tree, Map<ItemStack, Integer> donePromisses);
    void fullFill(LogisticsPromise promise, RequestItems destination);
    int getAvailableItemCount(ItemStack item);
    HashMap<ItemStack, Integer> getAllItems();
    Router getRouter();
}
