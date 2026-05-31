package net.danygames2014.logisticspipes.compat.ami;

import net.danygames2014.logisticspipes.screen.handler.RequestTableScreenHandler;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.RecipeLayout;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.VanillaRecipeCategoryUid;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.transfer.RecipeTransferError;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.transfer.RecipeTransferHandler;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.transfer.RecipeTransferInfo;
import net.glasslauncher.mods.alwaysmoreitems.transfer.BasicRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RequestTableRecipeTransferHandler extends BasicRecipeTransferHandler {
    public RequestTableRecipeTransferHandler() {
        super(new RecipeTransferInfo() {
            @Override
            public Class<? extends ScreenHandler> getContainerClass() {
                return RequestTableScreenHandler.class;
            }

            @Override
            public String getRecipeCategoryUid() {
                return VanillaRecipeCategoryUid.CRAFTING;
            }

            @Override
            public List<Slot> getRecipeSlots(ScreenHandler container) {
                List<Slot> slots = new ArrayList<>();
                RequestTableScreenHandler screenHandler = (RequestTableScreenHandler) container;
                for(Object oSlot : (container).slots) {
                    if(oSlot instanceof Slot slot && slot.inventory == screenHandler.craftingMatrix){
                        slots.add(slot);
                    }
                }
                return slots;
            }

            @Override
            public List<Slot> getInventorySlots(ScreenHandler container) {
                List<Slot> slots = new ArrayList<>();
                RequestTableScreenHandler screenHandler = (RequestTableScreenHandler) container;
                for(Object oSlot : (container).slots) {
                    if(oSlot instanceof Slot slot && (slot.inventory == screenHandler.playerInventory || slot.inventory == screenHandler.table.inv)){
                        slots.add(slot);
                    }
                }
                return slots;
            }
        });
    }
}
