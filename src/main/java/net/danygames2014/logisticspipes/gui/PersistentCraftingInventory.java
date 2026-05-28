package net.danygames2014.logisticspipes.gui;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class PersistentCraftingInventory extends CraftingInventory {
    private ScreenHandler handler;
    private Inventory parent;

    public PersistentCraftingInventory(ScreenHandler handler, Inventory parent) {
        super(handler, 3, 3);
        this.handler = handler;
        this.parent = parent;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot <= 9 ? this.parent.getStack(slot) : null;
    }

    @Override
    public ItemStack getStack(int x, int y) {
        if(x >= 0 && x < 3){
            int index = x + (y * 3);
            return this.getStack(index);
        }
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = parent.removeStack(slot, amount);
        if(stack != null){
            this.handler.onSlotUpdate(this);
        }

        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.parent.setStack(slot, stack);
        this.handler.onSlotUpdate(this);
    }
}
