package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class NetworkMonitorItem extends TemplateItem {
    public NetworkMonitorItem(Identifier identifier) {
        super(identifier);
        setMaxCount(1);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        if(world.getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
            GuiHelper.openGUI(user, LogisticsPipes.NAMESPACE.id("routing_stats"), null, new ModuleScreenHandler(user, null), (messagePacket) -> {
                messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, pipe.x, pipe.y, pipe.z};
            });
            return true;
        }
        return false;
    }
}
