package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.config.Config;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.network.RequestScreenContentC2SPacket;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.util.HashMap;
import java.util.LinkedList;

public class NormalOrderScreen extends OrderScreen{
    private enum DisplayOptions {
        Both,
        SupplyOnly,
        CraftOnly,
    }

    private HashMap<ItemIdentifier, Integer> availableItems;
    private LinkedList<ItemIdentifier> craftableItems;

    protected DisplayOptions displayOptions = DisplayOptions.Both;

    public NormalOrderScreen(RequestItems itemRequester, PlayerEntity player) {
        super(itemRequester, player);
        refreshItems();
    }

    @Override
    public void init() {
        super.init();
        buttons.add(new SmallButtonWidget(9, guiLeft + 10, bottom - 41, 46, 10, "Both"));
    }

    @Override
    public void specialItemRendering(ItemIdentifier item, int x, int y) {

    }

    @Override
    protected void refreshItems() {
        LogisticPipeBlockEntity requestPipe = (LogisticPipeBlockEntity)itemRequester;
        RequestHandler.DisplayOptions options;
        switch(displayOptions) {
            case SupplyOnly:
                options = RequestHandler.DisplayOptions.SupplyOnly;
                break;
            case CraftOnly:
                options = RequestHandler.DisplayOptions.CraftOnly;
                break;
            default:
                options = RequestHandler.DisplayOptions.Both;
        }
        PacketHelper.send(new RequestScreenContentC2SPacket(requestPipe.x, requestPipe.y, requestPipe.z, options));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if (button.id == 9) {
            String displayString = "";
            switch (displayOptions){
                case Both:
                    displayOptions = DisplayOptions.CraftOnly;
                    displayString = "Craft";
                    break;
                case CraftOnly:
                    displayOptions = DisplayOptions.SupplyOnly;
                    displayString = "Supply";
                    break;
                case SupplyOnly:
                    displayOptions = DisplayOptions.Both;
                    displayString = "Both";
                    break;
            }
            button.text = displayString;
            refreshItems();
        }
    }

    @Override
    boolean shouldCapAddAmount() {
        if(!Config.HUD_CONFIG.capAddAmount) {
            return false;
        }
        if(displayOptions == DisplayOptions.CraftOnly) {
            return false;
        }
        return true;
    }
}
