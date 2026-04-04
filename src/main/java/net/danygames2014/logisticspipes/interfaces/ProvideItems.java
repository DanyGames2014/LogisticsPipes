package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface ProvideItems {
    void canProvide(RequestTreeNode tree, Map<ItemIdentifier, Integer> donePromisses);
    void fullFill(LogisticsPromise promise, RequestItems destination);
    int getAvailableItemCount(ItemIdentifier item);
    HashMap<ItemIdentifier, Integer> getAllItems();
    Router getRouter();
}
