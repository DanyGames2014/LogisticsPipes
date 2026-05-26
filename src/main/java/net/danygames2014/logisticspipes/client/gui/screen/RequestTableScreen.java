package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.RequestTableLogisticPipeBlock;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.RequestTableLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.network.RequestScreenContentC2SPacket;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.danygames2014.logisticspipes.screen.handler.ChassisScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.RequestTableScreenHandler;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SimpleInventory;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class RequestTableScreen extends OrderScreen{
    private enum DisplayOptions {
        Both,
        SupplyOnly,
        CraftOnly,
    }

    protected DisplayOptions displayOptions = DisplayOptions.Both;
    public RequestTableLogisticPipeBlockEntity table;

    public RequestTableScreen(PlayerEntity player, RequestTableLogisticPipeBlockEntity table) {
        super(table, player, new RequestTableScreenHandler(player, table, 0, 0));
        this.table = table;

        this.backgroundWidth += this.getLeftAddition();

        refreshItems();
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft += this.getLeftAddition();
        buttons.add(new SmallButtonWidget(3, guiLeft + 10, bottom - 15, 46, 10, "Refresh")); // Refresh
        buttons.add(new SmallButtonWidget(13,  guiLeft + 10, bottom - 28, 46, 10, "Content")); // Component
        buttons.add(new SmallButtonWidget(9, guiLeft + 10, bottom - 41, 46, 10, "Both"));
        this.guiLeft -= this.getLeftAddition();
        buttons.add(new SmallButtonWidget(14, guiLeft + 75, guiTop + 55, 46, 10, "Request"));
    }

    @Override
    protected void drawBackground(float tickDelta) {
        super.drawBackground(tickDelta);
        for(int x = 0;x < 9;x++) {
            for(int y = 0;y < 3;y++) {
                BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + (x * 18) + 19, guiTop + (y * 18) + 79);
            }
        }
        for(int x = 0;x < 3;x++) {
            for(int y = 0;y < 3;y++) {
                BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + (x * 18) + 19, guiTop + (y * 18) + 14);
            }
        }
        BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + 100, guiTop + 32);
        BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + 163, guiTop + 50);
        fill(guiLeft + 75, guiTop + 38, guiLeft + 95, guiTop + 43, Colors.DarkGrey);
        for(int a = 0; a < 10;a++) {
            fill(guiLeft + 97 - a, guiTop + 40 - a, guiLeft + 98 - a, guiTop + 41 + a, Colors.DarkGrey);
        }
        for(int a = 0; a < 15;a++) {
            fill(guiLeft + 164 + a, guiTop + 51 + a, guiLeft + 166 + a, guiTop + 53 + a, Colors.DarkGrey);
            fill(guiLeft + 164 + a, guiTop + 65 - a, guiLeft + 166 + a, guiTop + 67 - a, Colors.DarkGrey);
        }
        BasicGuiHelper.drawPlayerInventoryBackground(minecraft, guiLeft + 20, guiTop + 150);
    }

    @Override
    protected void refreshItems() {
        LogisticPipeBlockEntity requestPipe = (LogisticPipeBlockEntity)itemRequester;
        RequestHandler.DisplayOptions options = switch (displayOptions) {
            case SupplyOnly -> RequestHandler.DisplayOptions.SupplyOnly;
            case CraftOnly -> RequestHandler.DisplayOptions.CraftOnly;
            default -> RequestHandler.DisplayOptions.Both;
        };
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
        } else if(button.id == 14) {
//            SimpleInventory compress = new SimpleInventory(9, "", Integer.MAX_VALUE);
//            for(int i=0; i < 9;i++) {
//                compress.addCompressed(_table.matrix.getStackInSlot(i));
//            }
//            for(int i=0; i < 9;i++) {
//                ItemStack slot = compress.getStackInSlot(i);
//                if(slot != null) MainProxy.sendPacketToServer(PacketHandler.getPacket(RequestSubmitPacket.class).setDimension(dimension).setStack(ItemIdentifierStack.GetFromStack(slot)).setPosX(xCoord).setPosY(yCoord).setPosZ(zCoord));
//            }
            refreshItems();
        }
    }

    @Override
    public void specialItemRendering(ItemIdentifier item, int x, int y) {

    }

    @Override
    protected int getLeftAddition() {
        return 200;
    }
}
