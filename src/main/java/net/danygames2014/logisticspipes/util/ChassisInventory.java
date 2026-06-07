package net.danygames2014.logisticspipes.util;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.minecraft.item.ItemStack;

public class ChassisInventory extends SimpleInventory{
    private ChassisLogisticPipeBlockEntity pipe;
    private boolean ignoreMarkDirty = false;
    public ChassisInventory(int size, String name, int stackLimit, MarkDirtyCallback markDirtyCallback, ChassisLogisticPipeBlockEntity pipe) {
        super(size, name, stackLimit, markDirtyCallback);
        this.pipe = pipe;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = super.getStack(slot);
        if(pipe.world != null) {
            ModuleItem.saveInformation(stack, pipe.getLogisticsModule().getSubModule(slot), pipe.world);
        }
        super.removeStack(slot, amount);
        return stack;
    }

    @Override
    public ItemStack getStack(int slot) {
        ItemStack stack = super.getStack(slot);
        if(pipe.world != null) {
            ModuleItem.saveInformation(stack, pipe.getLogisticsModule().getSubModule(slot), pipe.world);
        }
        return stack;
    }
}
