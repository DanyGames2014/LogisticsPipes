package net.danygames2014.logisticspipes.routing;

import net.danygames2014.logisticspipes.interfaces.ProvideItems;
import net.minecraft.item.ItemStack;

public class LogisticsPromise {
    public ItemStack item;
    public int numberOfItems;
    public ProvideItems sender;
    public boolean extra;

    public LogisticsPromise copy() {
        LogisticsPromise result = new LogisticsPromise();
        result.item = item;
        result.numberOfItems = numberOfItems;
        result.sender = sender;
        result.extra = extra;
        return result;
    }
}
