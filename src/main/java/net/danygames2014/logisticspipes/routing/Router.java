package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface Router {
    long getRouterId();
    LogisticPipeBlockEntity getPipe();
    void itemDropped(RoutedItemEntity routedItemEntity);

    Long2IntOpenHashMap getNeighborTable();

    void learnRoutesFromNeighbors();

    void learnRoutesToTargetFromNeighbors(long targetRouterId);

    boolean isRoutedExit(Direction direction);

    Long2ObjectOpenHashMap<RouteDestination> getRoutingTable();
}
