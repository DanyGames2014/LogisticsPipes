package net.danygames2014.logisticspipes.interfaces.routing;

import net.danygames2014.logisticspipes.interfaces.ProvideItems;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.request.CraftingTemplate;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface CraftItems extends ProvideItems, RequestItems {
    void registerExtras(int count);
    void addCrafting(LinkedList<CraftingTemplate> crafters);
    ItemStack getCraftedItem();
}
