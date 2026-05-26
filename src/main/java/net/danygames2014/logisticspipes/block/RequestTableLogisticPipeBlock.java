package net.danygames2014.logisticspipes.block;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntityFactory;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class RequestTableLogisticPipeBlock extends LogisticPipeBlock{
    public RequestTableLogisticPipeBlock(Identifier identifier, Material material, Identifier texture, @Nullable Identifier alternativeTexture, PipeType type, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter, PipeBlockEntityFactory blockEntityFactory) {
        super(identifier, material, texture, alternativeTexture, type, behavior, transporter, blockEntityFactory);
        setBoundingBox(0F, 0F, 0F, 1F, 1F, 1F);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!super.onUse(world, x, y, z, player)){
            if(world.getBlockEntity(x, y, z) instanceof RequestLogisticPipeBlockEntity pipe){
                if(player.getHand() != null && player.getHand().getItem() == LogisticsPipes.disk && pipe.getDiskInventory().getStack(0) == null) {
                    pipe.getDiskInventory().setStack(0, player.getHand());
                    player.inventory.main[player.inventory.selectedSlot] = null;
                    return true;
                }
                pipe.openModuleScreen(player);
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        return Box.createCached((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + this.maxY, (double)z + this.maxZ);
    }

    @Override
    public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
        Box var7 = this.getCollisionShape(world, x, y, z);
        if (var7 != null && box.intersects(var7)) {
            boxes.add(var7);
        }
    }

    @Override
    public HitResult raycast(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        return Block.STONE.raycast(world, x, y, z, startPos, endPos);
    }

    @Override
    public boolean isFullCube() {
        return true;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
