package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.minecraft.world.World;

public class ItemUtil {
    public static RoutedItem GetOrCreateRoutedItem(World worldObj, TravellingItemEntity itemEntity) {
        if (!isRoutedItem(itemEntity)){
            RoutedItemEntity newItem = new RoutedItemEntity(worldObj, itemEntity);
            return newItem;
        }
        return (RoutedItem) itemEntity;
    }

    public static boolean isRoutedItem(TravellingItemEntity item) {
        return (item instanceof RoutedItemEntity);
    }
}
