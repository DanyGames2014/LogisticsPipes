package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.SneakyDirectionReceiver;
import net.danygames2014.logisticspipes.screen.handler.ExtractorScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.SneakyDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

public class ExtractorScreen extends ScreenWithPrevious {
    private SneakyDirectionReceiver directionReceiver;
    private int slot;

    public ExtractorScreen(PlayerEntity player, LogisticPipeBlockEntity pipe, SneakyDirectionReceiver directionReceiver, Screen previousScreen, int slot) {
        super(player, pipe,new ExtractorScreenHandler(player, null), previousScreen);
        this.directionReceiver = directionReceiver;
        this.backgroundWidth = 160;
        this.backgroundHeight = 200;
        this.slot = slot;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        int left = width / 2 - backgroundWidth / 2;
        int top = height / 2 - backgroundHeight / 2;

        buttons.add(new ButtonWidget(0, left + 73, top + 23, 20, 20, ""));
        buttons.add(new ButtonWidget(1, left + 73, top + 43, 20, 20, ""));
        buttons.add(new ButtonWidget(2, left + 73, top + 63, 20, 20, ""));
        buttons.add(new ButtonWidget(3, left + 10, top + 43, 20, 20, ""));

        refreshButtons();
    }

    @Override
    protected void drawForeground() {
        refreshButtons();
        super.drawForeground();

        int left = width / 2 - backgroundWidth / 2;
        int top = height / 2 - backgroundHeight / 2;

        textRenderer.draw("Extract orientation", backgroundWidth / 2 - textRenderer.getWidth("Extract orientation") / 2, 10, 0x404040);
        textRenderer.draw("Default", 35, 50, 0x404040);
        textRenderer.draw("Top", 100, 30, 0x404040);
        textRenderer.draw("Side", 100, 50, 0x404040);
        textRenderer.draw("Bottom", 100, 70, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/extractor.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }

    private void refreshButtons() {
        for (Object p : buttons) {
            ButtonWidget button = (ButtonWidget) p;
            switch (button.id) {
                case 0:
                    button.text = isExtract(SneakyDirection.Top);
                    break;
                case 1:
                    button.text = isExtract(SneakyDirection.Side);
                    break;
                case 2:
                    button.text = isExtract(SneakyDirection.Bottom);
                    break;
                case 3:
                    button.text = isExtract(SneakyDirection.Default);
                    break;
            }
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                directionReceiver.setSneakyDirection(SneakyDirection.Top);
                break;

            case 1:
                directionReceiver.setSneakyDirection(SneakyDirection.Side);
                break;

            case 2:
                directionReceiver.setSneakyDirection(SneakyDirection.Bottom);
                break;

            case 3:
                directionReceiver.setSneakyDirection(SneakyDirection.Default);
                break;
        }

//        PacketDispatcher.sendPacketToServer(new PacketPipeInteger(NetworkConstants.EXTRACTOR_MODULE_DIRECTION_SET, pipe.xCoord, pipe.yCoord, pipe.zCoord, guibutton.id + (slot * 10)).getPacket());


        refreshButtons();
        super.buttonClicked(button);
    }

    private String getButtonText(boolean checked) {
        return checked ? "[X]" : "[ ]";
    }

    private String isExtract(SneakyDirection d) {
        return getButtonText(d == directionReceiver.getSneakyDirection());
    }
}
