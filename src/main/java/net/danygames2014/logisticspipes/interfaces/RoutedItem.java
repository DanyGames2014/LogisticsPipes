package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

/**
 * This interface describes the actions that must be available on an item that is considered routed
 * @author Krapht
 *
 */
public interface RoutedItem {
    enum TransportMode {
        Unknown,
        Default,
        Passive,
        Active
    }

    Long getDestination();
    void setDestination(Long destination);
    Long getSource();
    void setSource(Long source);

    void setTransportMode(TransportMode transportMode);
    TransportMode getTransportMode();

    void setDoNotBuffer(boolean doNotBuffer);
    boolean getDoNotBuffer();

    ItemStack getItemStack();

    TravellingItemEntity getTravellingItemEntity();

    RoutedItem split(World world, int itemsToTake, Direction direction);
    void setPosition(double x, double y, double z);
}
