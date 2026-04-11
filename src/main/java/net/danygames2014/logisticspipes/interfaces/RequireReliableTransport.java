package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.util.ItemIdentifier;

public interface RequireReliableTransport {
    void itemLost(ItemIdentifier item);

    void itemArrived(ItemIdentifier item);
}
