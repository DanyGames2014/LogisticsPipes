package net.danygames2014.logisticspipes.item.wrenchmode;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;

public class LearnRoutesWrenchMode extends WrenchMode {
    public LearnRoutesWrenchMode(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (world.getBlockEntity(x,y,z) instanceof LogisticPipeBlockEntity pipe) {
            pipe.learnRoutesFromNeighbors();
            return true;
        }

        return super.wrenchRightClick(stack, player, isSneaking, world, x, y, z, side, wrenchMode);
    }
}
