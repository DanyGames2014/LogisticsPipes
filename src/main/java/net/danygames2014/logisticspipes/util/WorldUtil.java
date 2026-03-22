package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.api.core.Position;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;

public class WorldUtil {
    public static LinkedList<AdjacentBlockEntity> getAdjacentBlockEntities(World world, int x, int y, int z) {
        LinkedList<AdjacentBlockEntity> discoveredBlockEntities = new LinkedList<>();
        for(Direction direction : Direction.values()){
            Position position = new Position(x, y, z, direction);
            position.moveForwards(1);
            BlockEntity blockEntity = world.getBlockEntity((int) position.x, (int) position.y, (int) position.z);

            if(blockEntity == null) continue;
            discoveredBlockEntities.add(new AdjacentBlockEntity(blockEntity, direction));
        }
        return discoveredBlockEntities;
    }

}
