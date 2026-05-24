package net.danygames2014.logisticspipes.capability.recipeprovider;

import net.danygames2014.logisticspipes.block.entity.CraftingPipeRecipe;
import net.danygames2014.nyalib.capability.block.BlockCapability;
import net.minecraft.entity.player.PlayerEntity;

public abstract class CraftingPipeRecipeProviderBlockCapability extends BlockCapability {
    public abstract boolean canOpen(PlayerEntity player);

    public abstract CraftingPipeRecipe getRecipe();
}
