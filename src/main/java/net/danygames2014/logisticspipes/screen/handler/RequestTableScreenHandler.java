package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.RequestTableLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public class RequestTableScreenHandler extends ModuleScreenHandler{
    public RequestTableScreenHandler(PlayerEntity player, RequestTableLogisticPipeBlockEntity table, int guiLeft, int guiTop) {
        super(player, table.inv);

        int i = 0;

        for(int y = 0;y < 3;y++) {
            for(int x = 0;x < 9;x++) {
                addNormalSlot(i++, table.inv, guiLeft + (x * 18) + 20, guiTop + (y * 18) + 80);
            }
        }

        i = 0;
        for(int y = 0;y < 3;y++) {
            for(int x = 0;x < 3;x++) {
                addNormalSlot(i++, table.matrix, guiLeft + (x * 18) + 20, guiTop + (y * 18) + 15);
            }
        }

        addNormalSlot(0, table.toSortInv, guiLeft + 164, guiTop + 51);
        addNormalSlotsForPlayerInventory(20, 150);
    }

    public RequestTableScreenHandler(PlayerEntity player, RequestTableLogisticPipeBlockEntity table) {
        this(player, table, 0, 0);
    }
}
