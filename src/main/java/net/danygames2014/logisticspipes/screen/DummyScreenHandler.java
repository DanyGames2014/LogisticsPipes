package net.danygames2014.logisticspipes.screen;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.*;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DummyScreenHandler extends ScreenHandler {
    private final Inventory playerInventory;
    private final Inventory dummyInventory;
    private final OpenScreenController controller;

    public DummyScreenHandler(Inventory playerInventory, Inventory dummyInventory){
        this.playerInventory = playerInventory;
        this.dummyInventory = dummyInventory;
        this.controller = null;
    }

    public DummyScreenHandler(PlayerEntity player, Inventory playerInventory, Inventory dummyInventory, OpenScreenController controller){
        this.playerInventory = playerInventory;
        this.dummyInventory = dummyInventory;
        this.controller = controller;
        controller.screenClosedByPlayer(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    /***
     * Adds all slots for the player inventory and hotbar
     * @param xOffset
     * @param yOffset
     */
    public void addNormalSlotsForPlayerInventory(int xOffset, int yOffset){
        if (playerInventory == null){
            return;
        }
        //Player "backpack"
        for(int row = 0; row < 3; row++) {
            for(int column = 0; column < 9; column++)
            {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, xOffset + column * 18, yOffset + row * 18));
            }
        }

        //Player "hotbar"
        for(int i1 = 0; i1 < 9; i1++) {
            addSlot(new Slot(playerInventory, i1, xOffset + i1 * 18, yOffset + 58));
        }
    }

    public void addDummySlot(int slotId, int x, int y){
        addSlot(new DummySlot(dummyInventory, slotId, x, y));
    }

    public void addNormalSlot(int slotId, Inventory inventory, int x, int y){
        addSlot(new Slot(inventory, slotId, x, y));
    }

    public void addRestrictedSlot(int slotId, Inventory inventory, int x, int y, int itemId) {
        addSlot(new RestrictedSlot(inventory, slotId, x, y, itemId));
    }

    public void addRestrictedSlot(int slotId, Inventory inventory, int x, int y, SlotCheck slotCheck) {
        addSlot(new RestrictedSlot(inventory, slotId, x, y, slotCheck));
    }

    public void addModuleSlot(int slotId, Inventory inventory, int x, int y, ChassisLogisticPipeBlockEntity pipe) {
        addSlot(new ModuleSlot(inventory, slotId, x, y, pipe));
    }

    @Override
    public ItemStack quickMove(int slot) {
        return null;
    }

    @Override
    public ItemStack onSlotClick(int index, int button, boolean shift, PlayerEntity player) {
        if (index < 0) return super.onSlotClick(index, button, shift, player);
        Slot slot = (Slot)slots.get(index);
        if (slot == null || !(slot instanceof DummySlot)) {
            ItemStack stack1 = super.onSlotClick(index, button, shift, player);
            ItemStack stack2 = slot.getStack();
//            if(stack2 != null && stack2.getItem().id == Configs.ItemModuleId + 256) {
//                if(player instanceof EntityPlayerMP && MainProxy.isServer(player.worldObj)) {
//                    ((EntityPlayerMP)player).updateCraftingInventorySlot(this, index, stack2);
//                }
//            }
            return stack1;
        }

        PlayerInventory inventoryplayer = player.inventory;

        ItemStack currentlyEquippedStack = inventoryplayer.getCursorStack();
        if (currentlyEquippedStack == null){
            if (slot.getStack() != null && button == 1){
                if (shift){
                    slot.getStack().count = Math.min(127, slot.getStack().count * 2);
                } else {
                    slot.getStack().count/=2;
                }
            }else{
                slot.setStack(null);
            }
            return currentlyEquippedStack;
        }

        if (!slot.hasStack()){
            slot.setStack(currentlyEquippedStack.copy());
            if (button == 1) {
                slot.getStack().count = 1;
            }
            if (slot.getStack().count > slot.getMaxItemCount()){
                slot.getStack().count = slot.getMaxItemCount();
            }

            return currentlyEquippedStack;
        }

        ItemIdentifier currentItem = ItemIdentifier.get(currentlyEquippedStack);
        ItemIdentifier slotItem = ItemIdentifier.get(slot.getStack());
        if (currentItem == slotItem){
            //Do manual shift-checking to play nice with NEI
            int counter = shift?10:1;
            if (button == 1 && slot.getStack().count + counter <= slot.getMaxItemCount()){
                slot.getStack().count += counter;
                return currentlyEquippedStack;
            }
            if (button == 0){
                if (slot.getStack().count - counter > 0){
                    slot.getStack().count-=counter;
                } else {
                    slot.setStack(null);
                }
                return currentlyEquippedStack;
            }
        } else {
            slot.setStack(currentlyEquippedStack.copy());
        }
        return currentlyEquippedStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        if(controller != null) {
            controller.screenClosedByPlayer(player);
        }
        super.onClosed(player);
    }
}
