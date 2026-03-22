package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.routing.Router;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public interface LogisticsManager {
    RoutedItem assignDestinationFor(World world, RoutedItem item, Long sourceRouterUUID, boolean excludeSource);
    RoutedItem destinationUnreachable(World world, RoutedItem item, Long currentRouter);
    boolean hasDestination(World world, ItemStack stack, boolean allowDefault, Long sourceRouter, boolean excludeSource);
    LinkedList<ItemStack> getCraftableItems(World world, Set<Router> validDestinations);
    HashMap<ItemStack, Integer> getAvailableItems(World world, Set<Router> validDestinations);
}
