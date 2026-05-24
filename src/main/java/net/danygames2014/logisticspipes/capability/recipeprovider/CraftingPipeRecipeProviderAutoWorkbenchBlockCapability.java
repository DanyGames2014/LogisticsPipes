package net.danygames2014.logisticspipes.capability.recipeprovider;

import net.danygames2014.buildcraft.block.entity.AutocraftingTableBlockEntity;
import net.danygames2014.logisticspipes.block.entity.CraftingPipeRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class CraftingPipeRecipeProviderAutoWorkbenchBlockCapability extends CraftingPipeRecipeProviderBlockCapability {
    private final AutocraftingTableBlockEntity autocraftingTable;
    
    public CraftingPipeRecipeProviderAutoWorkbenchBlockCapability(AutocraftingTableBlockEntity autocraftingTable) {
        this.autocraftingTable = autocraftingTable;
    }

    @Override
    public boolean canOpen(PlayerEntity player) {
        return autocraftingTable.canPlayerUse(player);
    }

    @Override
    public CraftingPipeRecipe getRecipe() {
        CraftingPipeRecipe recipe = new CraftingPipeRecipe();

        // Output
        ItemStack output = autocraftingTable.getStack(10);
        if (output != null) {
            recipe.setOutput(output.copy());
        }
        
        // Inputs
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = autocraftingTable.getStack(slot);
            if (stack != null) {
                recipe.addInput(stack.copy());
            }
        }
        
        return recipe;
    }
}
