package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.logisticspipes.util.RoutingUtil;
import net.minecraft.world.World;

import java.util.List;

public class LogisticsNetworkManager {
    private static final ObjectArrayList<LogisticsNetwork> networks = new ObjectArrayList<>();
    private static final Long2ObjectOpenHashMap<LogisticsNetwork> routerIdToNetworkMap = new Long2ObjectOpenHashMap<>();
    
    static {
        routerIdToNetworkMap.defaultReturnValue(null);
    }

    public static List<LogisticsNetwork> getNetworks() {
        return networks;
    }

    public static LogisticsNetwork fetchNetwork(World world, Router router) {
        if (!routerIdToNetworkMap.containsKey(router.getRouterId())) {
            // Discover the routers
            ObjectOpenHashSet<Router> routers = discoverNetwork(world, router);
            
            // Create the network
            LogisticsNetwork network = new LogisticsNetwork(routers);
            
            // Add all the router id -> network mappings
            for (Router routerInNetwork : network.routers) {
                routerIdToNetworkMap.put(routerInNetwork.getRouterId(), network);
            }
            networks.add(network);
        }
        
        return routerIdToNetworkMap.get(router.getRouterId());
    }

    private static ObjectOpenHashSet<Router> discoverNetwork(World world, Router router) {
        // Contains routers to be explored
        ObjectArrayFIFOQueue<Router> open = new ObjectArrayFIFOQueue<>();
        
        // Contains all the routers that have been discovered
        ObjectOpenHashSet<Router> closed = new ObjectOpenHashSet<>();
        
        // Initialize the starting point and add the starting router
        open.enqueue(router);
        
        while (!open.isEmpty()) {
            Router current = open.dequeue();
            
            // Check if this router has not already been closed
            if (closed.contains(current)) {
                continue;
            }

            // Discover the routers neigbors
            Long2IntOpenHashMap neighborTable = current.getNeighborTable();
            for (long neighborId : neighborTable.keySet()) {
                Router neighborRouter = RoutingUtil.getRouter(world, neighborId);
                if (!closed.contains(neighborRouter)) {
                    open.enqueue(RoutingUtil.getRouter(world, neighborId));
                }
            }
            
            closed.add(current);
        }

        return closed;
    }
    
    public static void invalidateNetwork(World world, Router router) {
        LogisticsNetwork network = routerIdToNetworkMap.get(router.getRouterId());
        
        // If the router is not in a cached network topology, we do still need to
        // notify the routers about the change of topology
        if (network == null) {
            ObjectOpenHashSet<Router> routers = discoverNetwork(world, router);
            
            for (Router routerInNetwork : routers) {
                routerInNetwork.topologyChanged();
            }
            
            return;
        }
        
        // Remove all the router id -> network mappings
        for (Router routerInNetwork : network.routers) {
            routerIdToNetworkMap.remove(routerInNetwork.getRouterId());
            routerInNetwork.topologyChanged();
        }
        
        // Remove the network from the network list
        networks.remove(network);
    }
}
