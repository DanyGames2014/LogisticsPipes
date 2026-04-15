package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.minecraft.entity.player.PlayerEntity;

public class OrderScreen extends LogisticsBaseScreen{
    public OrderScreen(RequestItems itemRequester, PlayerEntity player) {
        super(player, null, null);
    }
}
