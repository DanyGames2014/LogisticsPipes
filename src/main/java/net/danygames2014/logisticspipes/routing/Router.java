package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface Router {
    long getRouterId();

    LogisticsNetwork getNetwork();

    Long2IntOpenHashMap getNeighborTable();

    Long2ObjectOpenHashMap<RouteDestination> getRoutingTable();

    void learnRoutesFromNeighbors();

    void learnRoutesToTargetFromNeighbors(long targetRouterId);

    void itemDropped(RoutedItemEntity routedItemEntity);

    boolean isRoutedExit(Direction direction);

    LogisticPipeBlockEntity getPipe();
}
