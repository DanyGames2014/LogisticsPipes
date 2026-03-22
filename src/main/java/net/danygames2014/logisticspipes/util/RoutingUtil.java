package net.danygames2014.logisticspipes.util;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.routing.Router;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

public class RoutingUtil {
    /**
     * Packs X, Y, Z into a 64-bit long.
     * X: Bits 38-63 (26 bits)
     * Z: Bits 12-37 (26 bits)
     * Y: Bits 0-11  (12 bits)
     */
    public static long packRouterId(int x, int y, int z) {
        return (((long) x & 0x3FFFFFFL) << 38) |
                (((long) z & 0x3FFFFFFL) << 12) |
                ((long) y & 0xFFFL);
    }

    public static int unpackX(long routerId) {
        return (int) (routerId >> 38);
    }

    public static int unpackZ(long routerId) {
        return (int) ((routerId << 26) >> 38);
    }

    public static int unpackY(long routerId) {
        return (int) (routerId & 0xFFFL);
    }
    
    public static int getPipeMetric(World world, int x, int y, int z) {
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        
        if (blockEntity instanceof PipeBlockEntity pipe) {
            return getPipeMetric(pipe);
        }
        
        return -1;
    }
    
    public static int getPipeMetric(PipeBlockEntity pipe) {
        if (pipe instanceof Router) {
            return 1;
        }

        if (pipe.behavior == Buildcraft.stonePipeBehavior) {
            return 10;
        }

        if (pipe.behavior == Buildcraft.cobblestonePipeBehavior) {
            return 10;
        }

        if (pipe.behavior == Buildcraft.goldenPipeBehavior) {
            return 2;
        }

        if (pipe.behavior == Buildcraft.clayPipeBehavior) {
            return 10;
        }

        if (pipe.behavior == Buildcraft.sandstonePipeBehavior) {
            return 10;
        }

        return -1;
    }
    
    public static boolean isPipe(World world, int x, int y, int z) {
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);

        return blockEntity instanceof PipeBlockEntity;
    }
    
    public static Router getRouter(World world, long routerId) {
        int x = unpackX(routerId);
        int y = unpackY(routerId);
        int z = unpackZ(routerId);
        
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        
        if (blockEntity instanceof Router router) {
            return router;
        }
        
        return null;
    }
}
