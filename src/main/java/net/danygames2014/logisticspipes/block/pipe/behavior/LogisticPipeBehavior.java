package net.danygames2014.logisticspipes.block.pipe.behavior;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.buildcraft.util.MathUtil;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.routing.RouteDestination;
import net.danygames2014.logisticspipes.util.ItemUtil;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LogisticPipeBehavior extends PipeBehavior {
    @Override
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        boolean forcePacket = !(item instanceof RoutedItem routedItem) || routedItem.getDestination() == null;
        RoutedItem routedItem = ItemUtil.GetOrCreateRoutedItem(blockEntity.world, item);
        LogisticPipeBlockEntity pipe = (LogisticPipeBlockEntity)blockEntity;
        return pipe.getDirectionForItem(routedItem);
    }

    //    @Override
//    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
//        if (item instanceof RoutedItemEntity routedItem) {
//            // If there is no destination, we cant route
//            if (routedItem.destinationUUID == null) {
//                return null;
//            }
//
//            if (blockEntity instanceof LogisticPipeBlockEntity logisticsPipe) {
//                RouteDestination route = logisticsPipe.routingTable.getOrDefault(routedItem.destinationUUID.longValue(), null);
//
//                // If we do not know the route to destination, we cannot route
//                if (route == null) {
//                    return null;
//                }
//
//                int nextHopDirection = logisticsPipe.neighborTable.getOrDefault(route.routerId, -1);
//
//                // We do not know in which direction the router is
//                if (nextHopDirection == -1) {
//                    return null;
//                }
//
//                Direction direction = Direction.byId(nextHopDirection);
//
//                return validOutputDirections.contains(direction) ? direction : null;
//            }
//
//            return super.routeItem(blockEntity, validOutputDirections, routedItem);
//        }
//
//        // Non routed items cannot be routed
//        return null;
//    }

    @Override
    public ItemPipeTransporter.HandOffResult getFailedInsertResult(PipeBlockEntity blockEntity, ItemPipeTransporter transporter, TravellingItemEntity item) {
        return ItemPipeTransporter.HandOffResult.DROP;
    }

    @Override
    public double modifyItemSpeed(TravellingItemEntity item) {
        return MathUtil.clamp(item.speed * 8D, TravellingItemEntity.DEFAULT_SPEED * 8D, TravellingItemEntity.DEFAULT_SPEED * 20D);
    }

    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        PipeConnectionType type = super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior, side); 
        
        // If we can't connect to the other pipe, return NONE
        if (type == PipeConnectionType.NONE) {
            return type;
        }
        
        // Checks for pipes which we do not want to connect to
        if (otherPipeBehavior == Buildcraft.voidPipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        if (otherPipeBehavior == Buildcraft.ironPipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        if (otherPipeBehavior == Buildcraft.structurePipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        if (otherPipeBehavior == Buildcraft.obsidianPipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        if (otherPipeBehavior == Buildcraft.diamondPipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        if (blockEntity instanceof LogisticPipeBlockEntity logisticsPipe) {
            if (logisticsPipe.neighborTable.containsValue(side.getId())) {
                return PipeConnectionType.ALTERNATE;
            }
        }
        
        return type;
    }
}
