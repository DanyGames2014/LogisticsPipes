package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.block.entity.CraftingPipeRecipe;
import net.minecraft.entity.player.PlayerEntity;

public interface CraftingPipeRecipeProvider {
    boolean canOpen(PlayerEntity player);
    
    CraftingPipeRecipe getRecipe(); 
}
