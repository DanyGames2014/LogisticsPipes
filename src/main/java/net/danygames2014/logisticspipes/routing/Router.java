package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public interface Router {
    long getRouterId();

    Long2ObjectOpenHashMap<RouteDestination> getRoutingTable();
}
