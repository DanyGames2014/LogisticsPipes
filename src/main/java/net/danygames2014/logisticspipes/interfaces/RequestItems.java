package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.routing.Router;
import net.minecraft.item.ItemStack;

public interface RequestItems {
    Router getRouter();
    void itemCouldNotBeSend(ItemStack item);
}
