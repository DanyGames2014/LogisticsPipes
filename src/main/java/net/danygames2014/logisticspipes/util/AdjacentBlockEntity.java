package net.danygames2014.logisticspipes.util;

import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class AdjacentBlockEntity {
    public BlockEntity blockEntity;
    public Direction direction;

    public AdjacentBlockEntity(BlockEntity blockEntity, Direction direction) {
        this.blockEntity = blockEntity;
        this.direction = direction;
    }
}
