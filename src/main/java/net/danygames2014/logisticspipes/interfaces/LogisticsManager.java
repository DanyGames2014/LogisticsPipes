package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public interface LogisticsManager {
    RoutedItem assignDestinationFor(World world, RoutedItem item, long sourceRouterUUID, boolean excludeSource);

    RoutedItem destinationUnreachable(World world, RoutedItem item, long currentRouter);

    boolean hasDestination(World world, ItemStack stack, boolean allowDefault, long sourceRouter, boolean excludeSource);

    LinkedList<ItemIdentifier> getCraftableItems(World world, Set<Router> validDestinations);

    HashMap<ItemIdentifier, Integer> getAvailableItems(World world, Set<Router> validDestinations);
}
