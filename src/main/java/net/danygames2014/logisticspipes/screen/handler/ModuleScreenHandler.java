package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ModuleScreenHandler extends ScreenHandler {
    public PlayerEntity player;
    public final Inventory playerInventory;
    public final Inventory moduleInventory;
    
    public ModuleScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.moduleInventory = moduleInventory;
    }

    /***
     * Adds all slots for the player inventory and hotbar
     */
    public void addNormalSlotsForPlayerInventory(int xOffset, int yOffset) {
        if (playerInventory == null) {
            return;
        }
        // Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, xOffset + column * 18, yOffset + row * 18));
            }
        }

        // Player Hotbar
        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(playerInventory, slot, xOffset + slot * 18, yOffset + 58));
        }
    }

    public void addFilterSlot(int slotId, int x, int y) {
        addSlot(new FilterSlot(moduleInventory, slotId, x, y));
    }

    public void addNormalSlot(int slotId, Inventory inventory, int x, int y) {
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
        System.out.println("index = " + index + ", button = " + button + ", shift = " + shift + ", player = " + player);
        return super.onSlotClick(index, button, shift, player);
    }

    //    @Override
//    public ItemStack onSlotClick(int index, int button, boolean shift, PlayerEntity player) {
//        if (index < 0) return super.onSlotClick(index, button, shift, player);
//        Slot slot = (Slot) slots.get(index);
//        if (slot == null || !(slot instanceof DummySlot)) {
//            ItemStack stack1 = super.onSlotClick(index, button, shift, player);
//            ItemStack stack2 = slot.getStack();
////            if(stack2 != null && stack2.getItem().id == Configs.ItemModuleId + 256) {
////                if(player instanceof EntityPlayerMP && MainProxy.isServer(player.worldObj)) {
////                    ((EntityPlayerMP)player).updateCraftingInventorySlot(this, index, stack2);
////                }
////            }
//            return stack1;
//        }
//
//        PlayerInventory inventoryplayer = player.inventory;
//
//        ItemStack currentlyEquippedStack = inventoryplayer.getCursorStack();
//        if (currentlyEquippedStack == null) {
//            if (slot.getStack() != null && button == 1) {
//                if (shift) {
//                    slot.getStack().count = Math.min(127, slot.getStack().count * 2);
//                } else {
//                    slot.getStack().count /= 2;
//                }
//            } else {
//                slot.setStack(null);
//            }
//            return currentlyEquippedStack;
//        }
//
//        if (!slot.hasStack()) {
//            slot.setStack(currentlyEquippedStack.copy());
//            if (button == 1) {
//                slot.getStack().count = 1;
//            }
//            if (slot.getStack().count > slot.getMaxItemCount()) {
//                slot.getStack().count = slot.getMaxItemCount();
//            }
//
//            return currentlyEquippedStack;
//        }
//
//        ItemIdentifier currentItem = ItemIdentifier.get(currentlyEquippedStack);
//        ItemIdentifier slotItem = ItemIdentifier.get(slot.getStack());
//        if (currentItem == slotItem) {
//            //Do manual shift-checking to play nice with NEI
//            int counter = shift ? 10 : 1;
//            if (button == 1 && slot.getStack().count + counter <= slot.getMaxItemCount()) {
//                slot.getStack().count += counter;
//                return currentlyEquippedStack;
//            }
//            if (button == 0) {
//                if (slot.getStack().count - counter > 0) {
//                    slot.getStack().count -= counter;
//                } else {
//                    slot.setStack(null);
//                }
//                return currentlyEquippedStack;
//            }
//        } else {
//            slot.setStack(currentlyEquippedStack.copy());
//        }
//        return currentlyEquippedStack;
//    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
