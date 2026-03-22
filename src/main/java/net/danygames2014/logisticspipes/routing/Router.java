package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;

public interface Router {
    long getRouterId();
    LogisticPipeBlockEntity getPipe();
    void itemDropped(RoutedItemEntity routedItemEntity);

    Long2ObjectOpenHashMap<RouteDestination> getRoutingTable();
}
