package net.danygames2014.logisticspipes.util;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class NyalibInventoryUtil {
    public static boolean hasItemHandler(BlockEntity blockEntity){
        return CapabilityHelper.getCapability(blockEntity, ItemHandlerBlockCapability.class) != null;
    }

    public static ItemHandlerBlockCapabilityInventoryWrapper getWrappedItemHandler(BlockEntity blockEntity, Direction direction){
        return new ItemHandlerBlockCapabilityInventoryWrapper(CapabilityHelper.getCapability(blockEntity, ItemHandlerBlockCapability.class), direction);
    }
}
