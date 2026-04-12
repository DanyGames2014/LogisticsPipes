package net.danygames2014.logisticspipes.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FilterSlot extends Slot {
    public FilterSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        setStack(null);
        this.markDirty();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        setStack(new ItemStack(stack.getItem()));
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        this.markDirty();
        return null;
    }
}
