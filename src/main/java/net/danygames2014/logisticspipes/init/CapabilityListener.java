package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.capability.recipeprovider.CraftingPipeRecipeProviderAutoWorkbenchBlockCapabilityProvider;
import net.danygames2014.logisticspipes.capability.recipeprovider.CraftingPipeRecipeProviderBlockCapability;
import net.danygames2014.logisticspipes.capability.recipeprovider.CraftingPipeRecipeProviderInterfaceBlockCapabilityProvider;
import net.danygames2014.nyalib.event.BlockCapabilityClassRegisterEvent;
import net.danygames2014.nyalib.event.BlockCapabilityProviderRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;

public class CapabilityListener {
    @EventListener
    public void registerBlockCapabilityClass(BlockCapabilityClassRegisterEvent event) {
        event.register(LogisticsPipes.NAMESPACE.id("crafting_pipe_recipe_provider"), CraftingPipeRecipeProviderBlockCapability.class);
    }
    
    @EventListener
    public void registerBlockCapabilityProvider(BlockCapabilityProviderRegisterEvent event) {
        event.register(LogisticsPipes.NAMESPACE.id("crafting_pipe_interface_recipe_provider"), new CraftingPipeRecipeProviderInterfaceBlockCapabilityProvider());
        event.register(LogisticsPipes.NAMESPACE.id("crafting_pipe_auto_workbench_recipe_provider"), new CraftingPipeRecipeProviderAutoWorkbenchBlockCapabilityProvider());
    }
}
