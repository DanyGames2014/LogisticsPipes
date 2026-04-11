package net.danygames2014.logisticspipes.util;

import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerBlockCapabilityInventoryWrapper implements Inventory {

    private final ItemHandlerBlockCapability capability;
    @Nullable
    public Direction side;

    public ItemHandlerBlockCapabilityInventoryWrapper(ItemHandlerBlockCapability capability, @Nullable Direction side){
        this.capability = capability;
        this.side = side;
    }

    @Override
    public int size() {
        return capability.getInventory(side).length;
    }

    @Override
    public ItemStack getStack(int slot) {
        return capability.getItem(slot, side);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (capability.getItem(slot, side) != null) {
            if (capability.getItem(slot, side).count <= amount) {
                ItemStack var4 = capability.getItem(slot, side);
                capability.setItem(null, slot, side);
                return var4;
            } else {
                ItemStack var3 = capability.getItem(slot, side).split(amount);
                if (capability.getItem(slot, side).count == 0) {
                    capability.setItem(null, slot, side);
                }
                return var3;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        capability.setItem(stack, slot, side);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}
