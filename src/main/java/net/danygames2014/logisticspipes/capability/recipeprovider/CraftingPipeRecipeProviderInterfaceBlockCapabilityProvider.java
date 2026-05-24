package net.danygames2014.logisticspipes.capability.recipeprovider;

import net.danygames2014.logisticspipes.interfaces.CraftingPipeRecipeProvider;
import net.danygames2014.nyalib.capability.block.BlockCapabilityProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CraftingPipeRecipeProviderInterfaceBlockCapabilityProvider extends BlockCapabilityProvider<CraftingPipeRecipeProviderBlockCapability> {
    @Override
    public @Nullable CraftingPipeRecipeProviderBlockCapability getCapability(World world, int x, int y, int z) {
        if (world.getBlockEntity(x,y,z) instanceof CraftingPipeRecipeProvider provider) {
            return new CraftingPipeRecipeProviderInterfaceBlockCapability(provider);
        }
        
        return null;
    }
}
