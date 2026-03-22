package net.danygames2014.logisticspipes.block.pipe.behavior;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LogisticPipeBehavior extends PipeBehavior {
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
