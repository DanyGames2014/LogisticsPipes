package net.danygames2014.logisticspipes.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public class SupplierScreenHandler extends ModuleScreenHandler {
    public SupplierScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);

        addNormalSlotsForPlayerInventory(18, 97);

        int xOffset = 72;
        int yOffset = 18;

        // Filter Slots
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addFilterSlot(column + row * 3, xOffset + column * 18, yOffset + row * 18);
            }
        }
    }
}
