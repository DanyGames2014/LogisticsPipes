package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.CraftingLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public class CraftingPipeScreenHandler extends ModuleScreenHandler {
    public final CraftingLogisticPipeBlockEntity pipe;
    
    public CraftingPipeScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);
        this.pipe = (CraftingLogisticPipeBlockEntity) moduleInventory;

        addNormalSlotsForPlayerInventory(18, 97);

        for(int l = 0; l < 9; l++) {
            addFilterSlot(l, 18 + l * 18, 18);
        }

        addFilterSlot(9, 90, 64);
    }
}
