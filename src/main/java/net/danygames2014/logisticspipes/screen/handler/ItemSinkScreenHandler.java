package net.danygames2014.logisticspipes.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public class ItemSinkScreenHandler extends ModuleScreenHandler {
    public ItemSinkScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);

        addNormalSlotsForPlayerInventory(8, 60);

        // Filter Slots
        for (int pipeSlot = 0; pipeSlot < 9; pipeSlot++) {
            addFilterSlot(pipeSlot, 8 + pipeSlot * 18, 18);
        }
    }
}
