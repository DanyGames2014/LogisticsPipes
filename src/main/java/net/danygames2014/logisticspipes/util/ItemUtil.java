package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

public class ItemUtil {
    public static RoutedItem GetOrCreateRoutedItem(World worldObj, TravellingItemEntity itemEntity) {
        if (!isRoutedItem(itemEntity)) {
            RoutedItemEntity newItem = new RoutedItemEntity(worldObj, itemEntity);
            return newItem;
        }
        return (RoutedItem) itemEntity;
    }

    public static boolean isRoutedItem(TravellingItemEntity item) {
        return (item instanceof RoutedItemEntity);
    }

    public static RoutedItem createRoutedItem(ItemStack stack, World world) {
        TravellingItemEntity itemEntity = new TravellingItemEntity(world, 0, 0, 0, stack);
        return new RoutedItemEntity(world, itemEntity);
    }

    public static void spawnRoutedItem(World world, RoutedItem item, int x, int y, int z, Direction from) {
        ((TravellingItemEntity) item).toMiddle = true;
        ((TravellingItemEntity) item).travelDirection = from.getOpposite();
        ((TravellingItemEntity) item).lastTravelDirection = from.getOpposite();
        item.setPosition(x + 0.5D + (from.getOffsetX() * 0.5D), y + 0.25D + (from.getOffsetY() * 0.25D), z + 0.5D + (from.getOffsetZ() * 0.5D));
        world.spawnEntity((RoutedItemEntity) item);
    }

    public static void compress(List<ItemStack> input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stackOne = input.get(i);
            if (stackOne == null) continue;

            for (int j = i + 1; j < input.size(); j++) {
                ItemStack stackTwo = input.get(j);
                if (stackTwo == null) continue;

                if (stackOne.isItemEqual(stackTwo)) {
                    stackOne.count += stackTwo.count;
                    input.remove(j);
                    j--;
                }
            }
        }
    }

    public static ItemStack makeStack(ItemStack stack, int count) {
        ItemStack returnStack = stack.copy();
        returnStack.count = count;
        return returnStack;
    }
}
