package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.CheckBoxWidget;
import net.danygames2014.logisticspipes.gui.ScreenWithDisk;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.gui.popup.DiskPopupSubScreen;
import net.danygames2014.logisticspipes.network.DropDiskC2SPacket;
import net.danygames2014.logisticspipes.network.RequestDiskContentC2SPacket;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.util.LinkedList;

public class NormalOrderScreenMk2 extends NormalOrderScreen implements ScreenWithDisk {
    public RequestLogisticPipeBlockEntity pipe;
    private SmallButtonWidget Macrobutton;

    public NormalOrderScreenMk2(RequestLogisticPipeBlockEntity pipe, PlayerEntity player) {
        super(pipe, player);
        this.pipe = pipe;

        PacketHelper.send(new RequestDiskContentC2SPacket(pipe.x, pipe.y, pipe.z));
    }

    @Override
    public void init() {
        super.init();
        buttons.add(Macrobutton = new SmallButtonWidget(12, right - 55, bottom - 60, 50, 10, "Disk"));
        Macrobutton.active = false;
    }

    @Override
    protected void drawBackground(float tickDelta) {
        super.drawBackground(tickDelta);

        fill(right - 39, bottom - 47, right - 19, bottom - 27, Colors.Black);
        fill(right - 37, bottom - 45, right - 21, bottom - 29, Colors.DarkGrey);

        if(getDisk() != null) {
            itemRenderer.renderGuiItem(textRenderer, minecraft.textureManager, getDisk(), right - 37, bottom - 45);
            Macrobutton.active = true;
            Lighting.turnOff();
        } else {
            Macrobutton.active = false;
        }

//        //Click on Disk
//        if(lastClickedx != -10000000 &&	lastClickedy != -10000000) {
//            if (lastClickedx >= right - 39 && lastClickedx < right - 19 && lastClickedy >= bottom - 47 && lastClickedy < bottom - 27) {
//                PacketHelper.send(new DropDiskC2SPacket(pipe.x, pipe.y, pipe.z));
//                lastClickedx = -10000000;
//                lastClickedy = -10000000;
//            }
//        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if (button.id == 12) {
            PacketHelper.send(new RequestDiskContentC2SPacket(pipe.x, pipe.y, pipe.z));
            this.setSubScreen(new DiskPopupSubScreen(this));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (button == 0) {
            if (mouseX >= right - 39 && mouseX < right - 19 && mouseY >= bottom - 47 && mouseY < bottom - 27) {
                if (getDisk() != null) {
                    PacketHelper.send(new DropDiskC2SPacket(pipe.x, pipe.y, pipe.z));
                }
            }
        }
    }

    @Override
    public ItemStack getDisk(){
        return pipe.getDiskInventory().getStack(0);
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
        return pipe.x;
    }

    @Override
    public int getY() {
        return pipe.y;
    }

    @Override
    public int getZ() {
        return pipe.z;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }
}
