package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.init.WrenchModeListener;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.modificationstation.stationapi.api.util.Identifier;

public class RoutingDebugger extends WrenchBase {
    public RoutingDebugger(Identifier identifier) {
        super(identifier);
        addWrenchMode(WrenchModeListener.discoverNeighborsWrenchMode);
        addWrenchMode(WrenchModeListener.advertiseRouterWrenchMode);
        addWrenchMode(WrenchModeListener.learnRoutesWrenchMode);
        addWrenchMode(WrenchModeListener.clearRoutingTableWrenchMode);
        addWrenchMode(WrenchModeListener.discoverNetworkWrenchMode);
        addWrenchMode(WrenchModeListener.propagateRoutesWrenchMode);
        addWrenchMode(WrenchModeListener.discoverRoutersByMetricWrenchMode);
    }
}
