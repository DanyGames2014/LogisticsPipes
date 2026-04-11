package net.danygames2014.logisticspipes.item.wrenchmode;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.routing.LogisticsNetworkManager;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.List;

public class DiscoverRoutersByMetricWrenchMode extends WrenchMode {
    public DiscoverRoutersByMetricWrenchMode(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (world.getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
            long nanoTime = System.nanoTime();
            List<Router> routers = LogisticsNetworkManager.fetchRoutersByMetric(world, pipe);
            long nanoEnd = System.nanoTime();

            player.sendMessage("Discover routers took " + (nanoEnd - nanoTime) / 1000 + "us");
            for (Router router : routers) {
                player.sendMessage(router.getRouterId() + " -> " + router.getMetric(pipe.getRouterId()));
            }

            return true;
        }

        return super.wrenchRightClick(stack, player, isSneaking, world, x, y, z, side, wrenchMode);
    }
}
