package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.item.wrenchmode.*;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.event.WrenchModeRegistryEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class WrenchModeListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    public static WrenchMode discoverNeighborsWrenchMode;
    public static WrenchMode advertiseRouterWrenchMode;
    public static WrenchMode learnRoutesWrenchMode;
    public static WrenchMode clearRoutingTableWrenchMode;
    public static WrenchMode discoverNetworkWrenchMode;
    public static WrenchMode propagateRoutesWrenchMode;
    public static WrenchMode discoverRoutersByMetricWrenchMode;

    @EventListener
    public void registerWrenchModes(WrenchModeRegistryEvent event) {
        discoverNeighborsWrenchMode = new DiscoverNeighborsWrenchMode(NAMESPACE.id("discover_neighbors"));
        advertiseRouterWrenchMode = new AdvertiseRouterWrenchMode(NAMESPACE.id("advertise_router"));
        learnRoutesWrenchMode = new LearnRoutesWrenchMode(NAMESPACE.id("learn_routes"));
        clearRoutingTableWrenchMode = new ClearRoutingTableWrenchMode(NAMESPACE.id("clear_routing_table"));
        discoverNetworkWrenchMode = new DiscoverNetworkWrenchMode(NAMESPACE.id("discover_network"));
        propagateRoutesWrenchMode = new PropagateRoutesWrenchMode(NAMESPACE.id("propagate_routes"));
        discoverRoutersByMetricWrenchMode = new DiscoverRoutersByMetricWrenchMode(NAMESPACE.id("discover_routers_by_metric"));
    }
}
