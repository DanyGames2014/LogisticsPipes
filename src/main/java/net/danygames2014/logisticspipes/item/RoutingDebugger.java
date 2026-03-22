package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.init.WrenchModeListener;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.modificationstation.stationapi.api.util.Identifier;

public class RoutingDebugger extends WrenchBase {
    public RoutingDebugger(Identifier identifier) {
        super(identifier);
        addWrenchMode(WrenchModeListener.discoverNeighborsWrenchMode);
        addWrenchMode(WrenchModeListener.validateRoutesWrenchMode);
        addWrenchMode(WrenchModeListener.learnRoutesWrenchMode);
    }
}
