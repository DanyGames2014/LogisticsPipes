package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

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

    public static RoutedItem createRoutedItem(ItemStack stack, World world){
        TravellingItemEntity itemEntity = new TravellingItemEntity(world, 0, 0, 0, stack);
        return new RoutedItemEntity(world, itemEntity);
    }

    public static void compress(List<ItemStack> input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stackOne = input.get(i);
            if (stackOne == null) continue;

            for (int j = i + 1; j < input.size(); j++) {
                ItemStack stackTwo = input.get(j);
                if (stackTwo == null) continue;

                if (stackOne.isItemEqual(stackTwo)){
                    stackOne.count += stackTwo.count;
                    input.remove(j);
                    j--;
                }
            }
        }
    }
}
