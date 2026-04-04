package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.interfaces.routing.CraftItems;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CraftingTemplate {
    private ItemStack result;
    private CraftItems crafter;
    private HashMap<ItemStack, RequestItems> required = new HashMap<>();
    private final int priority;

    public CraftingTemplate(ItemStack result, CraftItems crafter, int priority) {
        this.result = result;
        this.crafter = crafter;
        this.priority = priority;
    }

    public void addRequirement(ItemStack stack, RequestItems crafter){
        required.put(stack, crafter);
    }

    public LogisticsPromise generatePromise(){
        LogisticsPromise promise = new LogisticsPromise();
        promise.item = result;
        promise.numberOfItems = result.count;
        promise.sender = crafter;
        return promise;
    }

    public List<Pair<ItemStack,RequestItems>> getSource() {
        List<Pair<ItemStack,RequestItems>> result = new ArrayList<>();
        for (ItemStack stack : required.keySet()) {
            result.add(new Pair<ItemStack, RequestItems>(stack,required.get(stack)));
        }
        return result;
    }

    public ItemStack getResultStack() {
        return result;
    }

    public CraftItems getCrafter(){
        return crafter;
    }

    public int getPriority() {
        return priority;
    }
}
