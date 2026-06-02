package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.RequestTableLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.config.Config;
import net.danygames2014.logisticspipes.gui.CheckBoxWidget;
import net.danygames2014.logisticspipes.gui.ScreenWithDisk;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.gui.popup.DiskPopupSubScreen;
import net.danygames2014.logisticspipes.network.DropDiskC2SPacket;
import net.danygames2014.logisticspipes.network.RequestDiskContentC2SPacket;
import net.danygames2014.logisticspipes.network.RequestScreenContentC2SPacket;
import net.danygames2014.logisticspipes.network.RequestTableRefillToggleC2SPacket;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.danygames2014.logisticspipes.screen.handler.RequestTableScreenHandler;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.util.LinkedList;

@SuppressWarnings("unchecked")
public class RequestTableScreen extends OrderScreen implements ScreenWithDisk {
    protected enum DisplayOptions {
        Both,
        SupplyOnly,
        CraftOnly,
    }

    protected DisplayOptions displayOptions = DisplayOptions.Both;
    public RequestTableLogisticPipeBlockEntity table;
    private SmallButtonWidget macroButton;

    public RequestTableScreen(PlayerEntity player, RequestTableLogisticPipeBlockEntity table) {
        super(table, player, new RequestTableScreenHandler(player, table, 0, 0));
        this.table = table;

        this.backgroundWidth += this.getLeftAddition();

        refreshItems();
    }

    @Override
    public void init() {
        super.init();
        buttons.add(new CheckBoxWidget(32, guiLeft + 75, guiTop + 56, 14, 14, table.refillMatrix));
        this.guiLeft += this.getLeftAddition();
        buttons.add(new SmallButtonWidget(9, guiLeft + 10, bottom - 41, 46, 10, "Both"));
        buttons.add(macroButton = new SmallButtonWidget(12, right - 55, bottom - 60, 50, 10, "Disk"));
        macroButton.active = false;

        this.guiLeft -= this.getLeftAddition();
    }

    @Override
    protected void drawBackground(float tickDelta) {
        super.drawBackground(tickDelta);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + (x * 18) + 19, guiTop + (y * 18) + 79);
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + (x * 18) + 19, guiTop + (y * 18) + 14);
            }
        }

        fill(right - 39, bottom - 47, right - 19, bottom - 27, Colors.Black);
        fill(right - 37, bottom - 45, right - 21, bottom - 29, Colors.DarkGrey);

        BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + 100, guiTop + 32);
        BasicGuiHelper.drawSlotBackground(minecraft, guiLeft + 163, guiTop + 50);
        fill(guiLeft + 75, guiTop + 38, guiLeft + 95, guiTop + 43, Colors.DarkGrey);
        for (int a = 0; a < 10; a++) {
            fill(guiLeft + 97 - a, guiTop + 40 - a, guiLeft + 98 - a, guiTop + 41 + a, Colors.DarkGrey);
        }
        for (int a = 0; a < 15; a++) {
            fill(guiLeft + 164 + a, guiTop + 51 + a, guiLeft + 166 + a, guiTop + 53 + a, Colors.DarkGrey);
            fill(guiLeft + 164 + a, guiTop + 65 - a, guiLeft + 166 + a, guiTop + 67 - a, Colors.DarkGrey);
        }
        BasicGuiHelper.drawPlayerInventoryBackground(minecraft, guiLeft + 20, guiTop + 150);

        if (getDisk() != null) {
            itemRenderer.renderGuiItem(textRenderer, minecraft.textureManager, getDisk(), right - 37, bottom - 45);
            Lighting.turnOff();
            macroButton.active = true;
        } else {
            macroButton.active = false;
        }

        if (buttons.get(11) instanceof CheckBoxWidget checkBoxWidget && checkBoxWidget.getState()) {
            textRenderer.draw("Refill", guiLeft + 91, guiTop + 60, 0x404040);
        } else {
            textRenderer.draw("Refill", guiLeft + 91, guiTop + 60, 0xA0A0A0);
        }
    }

    @Override
    protected void refreshItems() {
        LogisticPipeBlockEntity requestPipe = (LogisticPipeBlockEntity) itemRequester;
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
            switch (displayOptions) {
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
        } else if (button.id == 14) {
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
        if (button.id == 12) {
            PacketHelper.send(new RequestDiskContentC2SPacket(table.x, table.y, table.z));
            this.setSubScreen(new DiskPopupSubScreen(this));
        }
        if (button.id == 32) {
            CheckBoxWidget checkbox = (CheckBoxWidget) buttons.get(11);
            table.refillMatrix = checkbox.change();
            if (player.world.isRemote) {
                PacketHelper.send(new RequestTableRefillToggleC2SPacket(table.refillMatrix));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 0) {
            if (mouseX >= right - 39 && mouseX < right - 19 && mouseY >= bottom - 47 && mouseY < bottom - 27) {
                if (getDisk() != null) {
                    PacketHelper.send(new DropDiskC2SPacket(table.x, table.y, table.z));
                }
            }
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
        if(selectedItem != null && selectedItem.stackSize == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void specialItemRendering(ItemIdentifier item, int x, int y) {

    }

    @Override
    protected int getLeftAddition() {
        return 200;
    }

    @Override
    public ItemStack getDisk() {
        return table.getDiskInventory().getStack(0);
    }

    @Override
    public LogisticsBaseScreen getScreen() {
        return this;
    }

    @Override
    public LinkedList<ItemIdentifierStack> getAllItems() {
        return allItems;
    }

    @Override
    public int getX() {
        return table.x;
    }

    @Override
    public int getY() {
        return table.y;
    }

    @Override
    public int getZ() {
        return table.z;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }
}
