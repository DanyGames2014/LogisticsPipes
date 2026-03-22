package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.item.wrenchmode.ClearRoutingTableWrenchMode;
import net.danygames2014.logisticspipes.item.wrenchmode.DiscoverNeighborsWrenchMode;
import net.danygames2014.logisticspipes.item.wrenchmode.LearnRoutesWrenchMode;
import net.danygames2014.logisticspipes.item.wrenchmode.ValidateRoutesWrenchMode;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.event.WrenchModeRegistryEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class WrenchModeListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;
    
    public static WrenchMode discoverNeighborsWrenchMode;
    public static WrenchMode validateRoutesWrenchMode;
    public static WrenchMode learnRoutesWrenchMode;
    public static WrenchMode clearRoutingTableWrenchMode;
    
    @EventListener
    public void registerWrenchModes(WrenchModeRegistryEvent event) {
        discoverNeighborsWrenchMode = new DiscoverNeighborsWrenchMode(NAMESPACE.id("discover_neighbors"));
        validateRoutesWrenchMode = new ValidateRoutesWrenchMode(NAMESPACE.id("validate_routes"));
        learnRoutesWrenchMode = new LearnRoutesWrenchMode(NAMESPACE.id("learn_routes"));
        clearRoutingTableWrenchMode = new ClearRoutingTableWrenchMode(NAMESPACE.id("clear_routing_table"));
    }   
}
