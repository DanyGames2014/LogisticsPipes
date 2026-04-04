package net.danygames2014.logisticspipes.request;

import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.interfaces.routing.CraftItems;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CraftingTemplate {
    private ItemIdentifierStack result;
    private CraftItems crafter;
    private HashMap<ItemIdentifierStack, RequestItems> required = new HashMap<>();
    private final int priority;

    public CraftingTemplate(ItemIdentifierStack result, CraftItems crafter, int priority) {
        this.result = result;
        this.crafter = crafter;
        this.priority = priority;
    }

    public void addRequirement(ItemIdentifierStack stack, RequestItems crafter){
        required.put(stack, crafter);
    }

    public LogisticsPromise generatePromise(){
        LogisticsPromise promise = new LogisticsPromise();
        promise.item = result.getItem();
        promise.numberOfItems = result.stackSize;
        promise.sender = crafter;
        return promise;
    }

    public List<Pair<ItemIdentifierStack,RequestItems>> getSource() {
        List<Pair<ItemIdentifierStack,RequestItems>> result = new ArrayList<>();
        for (ItemIdentifierStack stack : required.keySet()) {
            result.add(new Pair<>(stack, required.get(stack)));
        }
        return result;
    }

    public ItemIdentifierStack getResultStack() {
        return result;
    }

    public CraftItems getCrafter(){
        return crafter;
    }

    public int getPriority() {
        return priority;
    }
}
