package net.danygames2014.logisticspipes.block;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntityFactory;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class RequestMk2LogisticPipeBlock extends LogisticPipeBlock{
    public RequestMk2LogisticPipeBlock(Identifier identifier, Material material, Identifier texture, @Nullable Identifier alternativeTexture, PipeType type, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter, PipeBlockEntityFactory blockEntityFactory) {
        super(identifier, material, texture, alternativeTexture, type, behavior, transporter, blockEntityFactory);
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
}
