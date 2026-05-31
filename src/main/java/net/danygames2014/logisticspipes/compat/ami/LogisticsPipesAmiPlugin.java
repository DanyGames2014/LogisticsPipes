package net.danygames2014.logisticspipes.compat.ami;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.glasslauncher.mods.alwaysmoreitems.api.*;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.transfer.RecipeTransferRegistry;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;

public class LogisticsPipesAmiPlugin implements ModPluginProvider {
    @Override
    public String getName() {
        return "LogisticsPipes";
    }

    @Override
    public Identifier getId() {
        return LogisticsPipes.NAMESPACE.id("logisticspipes");
    }

    @Override
    public void onAMIHelpersAvailable(AMIHelpers amiHelpers) {

    }

    @Override
    public void onItemRegistryAvailable(ItemRegistry itemRegistry) {

    }

    @Override
    public void register(ModRegistry registry) {
        RecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

        recipeTransferRegistry.addRecipeTransferHandler(new RequestTableRecipeTransferHandler());
    }

    @Override
    public void onRecipeRegistryAvailable(RecipeRegistry recipeRegistry) {

    }

    @Override
    public SyncableRecipe deserializeRecipe(NbtCompound recipe) {
        return null;
    }

    @Override
    public void updateBlacklist(AMIHelpers amiHelpers) {

    }
}
