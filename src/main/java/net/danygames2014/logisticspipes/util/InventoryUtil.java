package net.danygames2014.logisticspipes.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class InventoryUtil {
    private final Inventory inventory;
    private final boolean hideOne;

    public InventoryUtil(Inventory inventory, boolean hideOne) {
        this.inventory = inventory;
        this.hideOne = hideOne;
    }

    public int itemCount(final ItemStack item){
        int count = 0;
        for (int i = 0; i < inventory.size(); i++){
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            if (stack.isItemEqual(item)) {
                count += stack.count - (hideOne?1:0);
            }
        }
        return count;
    }

    public HashMap<ItemStack, Integer> getItemsAndCount(){
        HashMap<ItemStack, Integer> items = new HashMap<>();
        for (int i = 0; i < inventory.size(); i++){
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            int stackSize = stack.count - (hideOne?1:0);
            if (!items.containsKey(stack)){
                items.put(stack, stackSize);
            } else {
                items.put(stack, items.get(stack) + stackSize);
            }
        }
        return items;
    }

    public int getItemCount(ItemStack item){
        HashMap<ItemStack, Integer> itemsAndCount = getItemsAndCount();
        if (!itemsAndCount.containsKey(item)){
            return 0;
        }
        return itemsAndCount.get(item);
    }

    public ItemStack getSingleItem(ItemStack item){
        for (int i = 0; i < inventory.size(); i++){
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            if (hideOne && stack.count == 1) continue;
            if (stack.isItemEqual(item)) {
                ItemStack removed = stack.split(1);
                if (stack.count == 0){
                    inventory.setStack(i,  null);
                }
                return removed;
            }
        }
        return null;
    }

    public ItemStack getMultipleItems(ItemStack item, int count){
        if (itemCount(item) < count) return null;
        ItemStack stack = null;
        for (int i = 0; i < count; i++){
            if(stack == null){
                stack = getSingleItem(item);
            }
            else{
                stack.count += getSingleItem(item).count;
            }
        }
        return stack;
    }

    //Will not hide 1 item;
    public boolean containsItem(ItemStack item){
        for (int i = 0; i < inventory.size(); i++){
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            if (stack.isItemEqual(item)) return true;
        }
        return false;
    }

    //Will not hide 1 item;
    public int roomForItem(ItemStack item){
        int totalRoom = 0;
        for (int i = 0; i < inventory.size(); i++){
            ItemStack stack = inventory.getStack(i);
            if (stack == null){
                totalRoom += Math.min(inventory.getMaxCountPerStack(), item.getMaxCount());
                continue;
            }
            if (!stack.isItemEqual(item)) continue;

            totalRoom += (Math.min(inventory.getMaxCountPerStack(), item.getMaxCount()) - stack.count);
        }
        return totalRoom;

    }

    public boolean hasRoomForItem(ItemStack item) {
        return roomForItem(item) > 0;
    }
}
