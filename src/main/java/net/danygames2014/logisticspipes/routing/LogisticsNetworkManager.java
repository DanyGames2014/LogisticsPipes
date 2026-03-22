package net.danygames2014.logisticspipes.routing;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class LogisticsNetworkManager {
    public static ObjectArrayList<LogisticsNetwork> networks = new ObjectArrayList<>();
    public static Long2ObjectOpenHashMap<LogisticsNetwork> routerIdToNetworkMap = new Long2ObjectOpenHashMap<>();
    
    public static void routerUpdate(Router router) {
        LogisticsNetwork network;
        
        if (routerIdToNetworkMap.containsKey(router.getRouterId())) {
            network = routerIdToNetworkMap.get(router.getRouterId());
        } else {
            network = new LogisticsNetwork();
        }
        
        
        networks.add(network);
        routerIdToNetworkMap.put(router.getRouterId(), network);
    }
}
