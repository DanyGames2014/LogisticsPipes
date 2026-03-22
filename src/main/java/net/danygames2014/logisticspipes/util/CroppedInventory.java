package net.danygames2014.logisticspipes.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CroppedInventory implements Inventory {
    private final Inventory baseInventory;
    private final int cropStart;
    private final int cropEnd;

    public CroppedInventory(Inventory baseInventory, int cropStart, int cropEnd){
        if (baseInventory == null) throw new RuntimeException("PRECONDITION FAILED: baseInventory cannot be null");
        this.baseInventory = baseInventory;
        this.cropStart = cropStart;
        this.cropEnd = cropEnd;
    }

    @Override
    public int size() {
        return baseInventory.size() - (cropStart + cropEnd);
    }

    @Override
    public ItemStack getStack(int slot) {
        return baseInventory.getStack(slot + cropStart);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return baseInventory.removeStack(slot + cropStart, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        baseInventory.setStack(slot + cropStart, stack);
    }

    @Override
    public String getName() {
        return baseInventory.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return baseInventory.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        baseInventory.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return baseInventory.canPlayerUse(player);
    }
}
