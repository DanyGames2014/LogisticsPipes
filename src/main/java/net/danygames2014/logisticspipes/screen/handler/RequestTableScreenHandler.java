package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.RequestTableLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.CraftingRefillSlot;
import net.danygames2014.logisticspipes.gui.PersistentCraftingInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.CraftingRecipeManager;
import net.minecraft.screen.slot.Slot;

public class RequestTableScreenHandler extends ModuleScreenHandler{

    public RequestTableLogisticPipeBlockEntity table;

    public CraftingInventory craftingMatrix;
    public Inventory craftingResult;
    public Slot craftingResultSlot;

    public RequestTableScreenHandler(PlayerEntity player, RequestTableLogisticPipeBlockEntity table, int guiLeft, int guiTop) {
        super(player, table.inv);

        this.table = table;

        this.craftingMatrix = new PersistentCraftingInventory(this, table.matrix);
        this.craftingResult = new CraftingResultInventory();

        this.craftingResultSlot = new CraftingRefillSlot(this, player, craftingMatrix, craftingResult, 0, guiLeft + 101, guiTop + 33);
        this.addSlot(this.craftingResultSlot);

        int i = 0;

        for(int y = 0;y < 3;y++) {
            for(int x = 0;x < 9;x++) {
                addNormalSlot(i++, table.inv, guiLeft + (x * 18) + 20, guiTop + (y * 18) + 80);
            }
        }

        i = 0;
        for(int y = 0;y < 3;y++) {
            for(int x = 0;x < 3;x++) {
                addNormalSlot(i++, craftingMatrix, guiLeft + (x * 18) + 20, guiTop + (y * 18) + 15);
            }
        }

        addNormalSlot(0, table.toSortInv, guiLeft + 164, guiTop + 51);
        addNormalSlotsForPlayerInventory(20, 150);

        updateCraftingResult();
    }

    public RequestTableScreenHandler(PlayerEntity player, RequestTableLogisticPipeBlockEntity table) {
        this(player, table, 0, 0);
    }

    public void updateCraftingResult() {
        this.craftingResultSlot.setStack(CraftingRecipeManager.getInstance().craft(this.craftingMatrix));
    }

    @Override
    public void onSlotUpdate(Inventory inventory) {
        updateCraftingResult();
        super.onSlotUpdate(inventory);
    }
}
