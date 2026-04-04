package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.item.ItemStack;

public interface RequestItems {
    Router getRouter();
    void itemCouldNotBeSend(ItemIdentifierStack item);
}
