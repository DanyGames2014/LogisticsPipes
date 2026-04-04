package net.danygames2014.logisticspipes.util;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class ItemMessage {
    public Item item;
    public int data;
    public int amount;
    public NbtCompound nbt;

    public ItemMessage() {}

    public ItemMessage(Item item, int data, int amount, NbtCompound nbt){
        this.item = item;
        this.data = data;
        this.amount = amount;
        this.nbt = nbt;
    }

    public ItemMessage(ItemIdentifier selectedItem, int requestCount){
        this(selectedItem.item, selectedItem.itemDamage, requestCount, selectedItem.nbt);
    }

    public ItemMessage(ItemIdentifierStack selectedItemStack) {
        this(selectedItemStack.getItem(), selectedItemStack.stackSize);
    }

    @Override
    public String toString() {
        return amount + " " + ItemIdentifier.get(item,data,nbt).getFriendlyName();
    }

    public ItemIdentifier getItemIdentifier() {
        return ItemIdentifier.get(item, data, nbt);
    }

    public static void compress(List<ItemMessage> input) {
        for(int i=0;i<input.size();i++) {
            for(int j=i+1;j<input.size();j++) {
                ItemMessage one = input.get(i);
                ItemMessage two = input.get(j);
                if(one.item == two.item && one.data == two.data && one.nbt == two.nbt) {
                    one.amount += two.amount;
                    input.remove(j);
                }
            }
        }
    }
}
