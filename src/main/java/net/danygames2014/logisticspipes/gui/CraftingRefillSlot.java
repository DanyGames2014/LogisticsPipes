package net.danygames2014.logisticspipes.gui;

import net.danygames2014.logisticspipes.screen.handler.RequestTableScreenHandler;
import net.minecraft.achievement.Achievements;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;

public class CraftingRefillSlot extends CraftingResultSlot {
    private Inventory input;
    private PlayerEntity player;
    private RequestTableScreenHandler handler;

    public CraftingRefillSlot(RequestTableScreenHandler handler, PlayerEntity player, Inventory craftingMatrix, Inventory craftingResult, int index, int x, int y) {
        super(player, craftingMatrix, craftingResult, index, x, y);
        this.player = player;
        this.input = craftingMatrix;
        this.handler = handler;
    }

    public void onTakeItem(ItemStack craftedStack) {
        craftedStack.onCraft(this.player.world, this.player);
        if (craftedStack.itemId == Block.CRAFTING_TABLE.id) {
            this.player.increaseStat(Achievements.CRAFT_WORKBENCH, 1);
        } else if (craftedStack.itemId == Item.WOODEN_PICKAXE.id) {
            this.player.increaseStat(Achievements.CRAFT_PICKAXE, 1);
        } else if (craftedStack.itemId == Block.FURNACE.id) {
            this.player.increaseStat(Achievements.CRAFT_FURNACE, 1);
        } else if (craftedStack.itemId == Item.WOODEN_HOE.id) {
            this.player.increaseStat(Achievements.CRAFT_HOE, 1);
        } else if (craftedStack.itemId == Item.BREAD.id) {
            this.player.increaseStat(Achievements.CRAFT_BREAD, 1);
        } else if (craftedStack.itemId == Item.CAKE.id) {
            this.player.increaseStat(Achievements.CRAFT_CAKE, 1);
        } else if (craftedStack.itemId == Item.STONE_PICKAXE.id) {
            this.player.increaseStat(Achievements.CRAFT_STONE_PICKAXE, 1);
        } else if (craftedStack.itemId == Item.WOODEN_SWORD.id) {
            this.player.increaseStat(Achievements.CRAFT_SWORD, 1);
        }

        for (int i = 0; i < this.input.size(); ++i) {
            ItemStack stack = this.input.getStack(i);

            if (stack != null) {
                ItemStack removedStack = this.input.removeStack(i, 1);

                if (stack.getItem().hasCraftingReturnItem()) {
                    this.input.setStack(i, new ItemStack(stack.getItem().getCraftingReturnItem()));
                }

                // The stack became null, try to refill it
                if (input.getStack(i) == null) {
                    for (int j = 0; j <= this.handler.table.inv.size(); ++j) {
                        ItemStack potentialStack = this.handler.table.inv.getStack(j);
                        System.out.println("Index: " + j + " Stack: " + potentialStack);
                        if(potentialStack != null && potentialStack.isItemEqual(removedStack)) {
                            input.setStack(i, handler.table.inv.removeStack(j, 1));
                            break;
                        }
                    }
                }
            }
        }
    }
}
