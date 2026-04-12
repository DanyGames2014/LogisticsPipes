package net.danygames2014.logisticspipes.gui;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ModuleSlot extends RestrictedSlot {
    private final ChassisLogisticPipeBlockEntity pipe;
    private final int moduleIndex;

    public ModuleSlot(Inventory inventory, int index, int x, int y, ChassisLogisticPipeBlockEntity pipe) {
        super(inventory, index, x, y, ModuleSlot::isStackAllowed);
        this.pipe = pipe;
        this.moduleIndex = index;
    }

    private static boolean isStackAllowed(ItemStack itemStack) {
        return itemStack.getItem() instanceof ModuleItem;
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        super.onTakeItem(stack);
        ModuleItem.saveInformation(stack, pipe.getLogisticsModule().getSubModule(moduleIndex), pipe.world);
    }
}
