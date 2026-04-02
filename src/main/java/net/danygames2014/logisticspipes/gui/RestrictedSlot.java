package net.danygames2014.logisticspipes.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class RestrictedSlot extends Slot {
    private final int itemId;
    private final SlotCheck slotCheck;

    public RestrictedSlot(Inventory inventory, int index, int x, int y, int itemId) {
        super(inventory, index, x, y);
        this.itemId = itemId;
        this.slotCheck = null;
    }

    public RestrictedSlot(Inventory inventory, int index, int x, int y, SlotCheck slotCheck) {
        super(inventory, index, x, y);
        this.itemId = -1;
        this.slotCheck = slotCheck;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean canInsert(ItemStack stack) {
        if(slotCheck == null) {
            return stack.itemId == itemId;
        } else {
            return slotCheck.isStackAllowed(stack);
        }
    }
}
