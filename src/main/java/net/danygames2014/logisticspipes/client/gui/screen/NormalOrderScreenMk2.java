package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.gui.popup.DiskPopupSubScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class NormalOrderScreenMk2 extends NormalOrderScreen {
    public RequestLogisticPipeBlockEntity pipe;
    private SmallButtonWidget Macrobutton;

    public NormalOrderScreenMk2(RequestLogisticPipeBlockEntity pipe, PlayerEntity player) {
        super(pipe, player);
        this.pipe = pipe;

//        PacketDispatcher.sendPacketToServer(new PacketCoordinates(NetworkConstants.DISK_REQUEST_CONTENT, pipe.xCoord, pipe.yCoord, pipe.zCoord).getPacket());
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
        } else {
            Macrobutton.active = false;
        }

        //Click on Disk
        if(lastClickedx != -10000000 &&	lastClickedy != -10000000) {
            if (lastClickedx >= right - 39 && lastClickedx < right - 19 && lastClickedy >= bottom - 47 && lastClickedy < bottom - 27) {
//                PacketDispatcher.sendPacketToServer(new PacketCoordinates(NetworkConstants.DISK_DROP, pipe.xCoord, pipe.yCoord, pipe.zCoord).getPacket());
                lastClickedx = -10000000;
                lastClickedy = -10000000;
            }
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if (button.id == 12) {
//            PacketDispatcher.sendPacketToServer(new PacketCoordinates(NetworkConstants.DISK_REQUEST_CONTENT, pipe.xCoord, pipe.yCoord, pipe.zCoord).getPacket());
            this.setSubScreen(new DiskPopupSubScreen(this));
        }
    }

    public ItemStack getDisk(){
        return pipe.getDiskInventory().getStack(0);
    }
}
