package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.logisticspipes.util.RoutingUtil;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class LogisticsNetworkManager {
    private static final ObjectArrayList<LogisticsNetwork> networks = new ObjectArrayList<>();
    private static final Long2ObjectOpenHashMap<LogisticsNetwork> routerIdToNetworkMap = new Long2ObjectOpenHashMap<>(128, 0.5F);
    private static final Long2ObjectOpenHashMap<List<Router>> routeIdToRoutersByMetric = new Long2ObjectOpenHashMap<>(128, 0.5F);
    
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
    
    public static List<Router> fetchRoutersByMetric(World world, Router router) {
        if (!routeIdToRoutersByMetric.containsKey(router.getRouterId())) {
            routeIdToRoutersByMetric.put(router.getRouterId(), discoverRoutersByMetric(world, router));
        }
        
        return routeIdToRoutersByMetric.get(router.getRouterId());
    }
    
    private static List<Router> discoverRoutersByMetric(World world, Router originRouter) {
        // All the available routers
        ObjectArrayList<Router> routers = new ObjectArrayList<>(fetchNetwork(world, originRouter).routers);
        
        // Advertise the routers so there's actual routes
        for (Router router : routers) {
            router.smartAdvertiseRouter();
        }
        routers.remove(originRouter);
        
        // The metrics from the origin router
        Object2IntOpenHashMap<Router> routerIdToMetric = new Object2IntOpenHashMap<>(routers.size());
        for (Router routerInNetwork : routers) {
            routerIdToMetric.put(routerInNetwork, routerInNetwork.getMetric(originRouter.getRouterId()));
        }
        
        // Remove invalid routes
        routers.removeIf(router -> routerIdToMetric.getInt(router) == -1);
        
        // Sort the routers by metric
        routers.sort(Comparator.comparingInt(routerIdToMetric::getInt));
        
        return routers;
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
            if (current != null) {
                Long2IntOpenHashMap neighborTable = current.getNeighborTable();
                for (long neighborId : neighborTable.keySet()) {
                    Router neighborRouter = RoutingUtil.getRouter(world, neighborId);
                    if (!closed.contains(neighborRouter)) {
                        open.enqueue(RoutingUtil.getRouter(world, neighborId));
                    }
                }

                closed.add(current);
            }
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
            routeIdToRoutersByMetric.remove(routerInNetwork.getRouterId());
            routerInNetwork.topologyChanged();
        }

        // Remove the network from the network list
        networks.remove(network);
    }
}
