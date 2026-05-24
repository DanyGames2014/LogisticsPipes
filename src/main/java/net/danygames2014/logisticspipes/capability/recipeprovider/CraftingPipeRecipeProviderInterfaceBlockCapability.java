package net.danygames2014.logisticspipes.capability.recipeprovider;

import net.danygames2014.logisticspipes.block.entity.CraftingPipeRecipe;
import net.danygames2014.logisticspipes.interfaces.CraftingPipeRecipeProvider;
import net.minecraft.entity.player.PlayerEntity;

public class CraftingPipeRecipeProviderInterfaceBlockCapability extends CraftingPipeRecipeProviderBlockCapability {
    private final CraftingPipeRecipeProvider provider;

    public CraftingPipeRecipeProviderInterfaceBlockCapability(CraftingPipeRecipeProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean canOpen(PlayerEntity player) {
        return provider.canOpen(player);
    }

    @Override
    public CraftingPipeRecipe getRecipe() {
        return provider.getRecipe();
    }
}
