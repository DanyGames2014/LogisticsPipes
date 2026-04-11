package net.danygames2014.logisticspipes.util;

import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.util.tuple.Pair3;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

import java.util.LinkedList;

public class ItemIdentifierStack {
    private final ItemIdentifier item;
    public int stackSize;

    public static ItemIdentifierStack getFromStack(ItemStack stack) {
        return new ItemIdentifierStack(ItemIdentifier.get(stack), stack.count);
    }

    public ItemIdentifierStack(ItemIdentifier item, int stackSize){
        this.item = item;
        this.stackSize = stackSize;
    }

    public ItemIdentifier getItem(){
        return item;
    }

    public ItemStack makeNormalStack(){
        ItemStack stack = new ItemStack(item.item, this.stackSize, item.itemDamage);
        StationNBTSetter.cast(stack).setStationNbt(item.nbt);
        return stack;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof ItemIdentifierStack stack) {
            return stack.item.equals(this.item) && stack.stackSize == this.stackSize;
        }
        return false;
    }

    @Override
    public String toString() {
        return stackSize + "x" + item.toString();
    }

    @Override
    public ItemIdentifierStack clone() {
        return new ItemIdentifierStack(item, stackSize);
    }

    public static LinkedList<ItemIdentifierStack> getListFromInventory(Inventory inv) {
        return getListFromInventory(inv, false);
    }

    public static LinkedList<ItemIdentifierStack> getListFromInventory(Inventory inv, boolean removeNull) {
        LinkedList<ItemIdentifierStack> list = new LinkedList<>();
        for(int i=0;i<inv.size();i++) {
            if(inv.getStack(i) == null) {
                if(!removeNull) {
                    list.add(null);
                }
            } else {
                list.add(ItemIdentifierStack.getFromStack(inv.getStack(i)));
            }
        }
        return list;
    }

    public static LinkedList<ItemIdentifierStack> getListSendQueue(LinkedList<Pair3<RoutedItem, Direction, ItemSendMode>> _sendQueue) {
        LinkedList<ItemIdentifierStack> list = new LinkedList<ItemIdentifierStack>();
        for(Pair3<RoutedItem, Direction, ItemSendMode> part:_sendQueue) {
            if(part == null) {
                list.add(null);
            } else {
                boolean added = false;
                for(ItemIdentifierStack stack:list) {
                    if(stack.getItem().equals(ItemIdentifierStack.getFromStack(part.getValue1().getItemStack()).getItem())) {
                        stack.stackSize += part.getValue1().getItemStack().count;
                        added = true;
                        break;
                    }
                }
                if(!added) {
                    list.add(ItemIdentifierStack.getFromStack(part.getValue1().getItemStack()));
                }
            }
        }
        return list;
    }
}
