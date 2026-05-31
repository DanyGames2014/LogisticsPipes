package net.danygames2014.logisticspipes.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;
import java.util.Set;

public class InventoryUtil {
    private final Inventory inventory;
    private final boolean hideOne;

    public InventoryUtil(Inventory inventory, boolean hideOne) {
        this.inventory = inventory;
        this.hideOne = hideOne;

    }

    public int itemCount(final ItemIdentifier item) {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            if (ItemIdentifier.get(stack) == item) {
                count += stack.count - (hideOne ? 1 : 0);
            }
        }
        return count;
    }

    public HashMap<ItemIdentifier, Integer> getItemsAndCount() {
        HashMap<ItemIdentifier, Integer> items = new HashMap<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            ItemIdentifier itemId = ItemIdentifier.get(stack);
            int stackSize = stack.count - (hideOne ? 1 : 0);
            if (!items.containsKey(itemId)) {
                items.put(itemId, stackSize);
            } else {
                items.put(itemId, items.get(itemId) + stackSize);
            }
        }
        return items;
    }

    public int getItemCount(ItemIdentifier item) {
        HashMap<ItemIdentifier, Integer> itemsAndCount = getItemsAndCount();
        if (!itemsAndCount.containsKey(item)) {
            return 0;
        }
        return itemsAndCount.get(item);
    }

    public ItemStack getSingleItem(ItemIdentifier item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            if (hideOne && stack.count == 1) continue;
            if (ItemIdentifier.get(stack) == item) {
                ItemStack removed = stack.split(1);
                if (stack.count == 0) {
                    inventory.setStack(i, null);
                }
                return removed;
            }
        }
        return null;
    }

    public ItemStack getMultipleItems(ItemIdentifier item, int count) {
        if (itemCount(item) < count) return null;
        ItemStack stack = null;
        for (int i = 0; i < count; i++) {
            if (stack == null) {
                stack = getSingleItem(item);
            } else {
                stack.count += getSingleItem(item).count;
            }
        }
        return stack;
    }

    //Will not hide 1 item;
    public boolean containsItem(ItemIdentifier item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            if (ItemIdentifier.get(stack) == item) return true;
        }
        return false;
    }

    public boolean containsModItem(ItemIdentifier item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            Identifier itemId = ItemRegistry.INSTANCE.getId(item.item);
            Identifier otherId = ItemRegistry.INSTANCE.getId(stack.getItem());
            if(itemId != null && otherId != null && itemId.namespace == otherId.namespace) return true;
        }
        return false;
    }

    public boolean containsItemTag(ItemIdentifier item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) continue;
            Set<TagKey<Item>> tags = stack.getItem().getRegistryEntry().getTags();
            for(TagKey<Item> tag : item.item.getRegistryEntry().getTags()) {
                if(tags.contains(tag)) return true;
            }
        }
        return false;
    }

    //Will not hide 1 item;
    public int roomForItem(ItemIdentifier item) {
        int totalRoom = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack == null) {
                totalRoom += Math.min(inventory.getMaxCountPerStack(), item.makeNormalStack(1).getMaxCount());
                continue;
            }
            if (ItemIdentifier.get(stack) != item) continue;

            totalRoom += (Math.min(inventory.getMaxCountPerStack(), item.makeNormalStack(1).getMaxCount()) - stack.count);
        }
        return totalRoom;

    }

    public boolean hasRoomForItem(ItemIdentifier item) {
        return roomForItem(item) > 0;
    }
}
