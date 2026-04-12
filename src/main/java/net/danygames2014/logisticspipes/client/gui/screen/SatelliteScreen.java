package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.SatelliteLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class SatelliteScreen extends LogisticsBaseScreen{
    SatelliteLogisticPipeBlockEntity pipe;
    PlayerEntity player;

    public SatelliteScreen(SatelliteLogisticPipeBlockEntity pipe, PlayerEntity player) {
        super(player, pipe,  new ModuleScreenHandler(player, null));

        this.pipe = pipe;
        this.player = player;

        this.backgroundWidth = 116;
        this.backgroundHeight = 70;
    }

    @Override
    public void init() {
        super.init();

        buttons.add(new ButtonWidget(0, (width / 2) - (30 /2) + 35, (height / 2) - (20 / 2), 30, 20, "+"));
        buttons.add(new ButtonWidget(1, (width / 2) - (30 /2) - 35, (height / 2) - (20 / 2), 30, 20, "-"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(pipe == null) {
            return;
        }
        if(button.id == 0) {
            pipe.setNextId(player);
        }

        if(button.id == 1) {
            pipe.setPrevId(player);
        }
        super.buttonClicked(button);
    }

    @Override
    protected void drawForeground() {
        super.drawForeground();
        textRenderer.draw("Satellite ID", 33, 10, 0x404040);
        if(pipe == null) return;
        textRenderer.draw(pipe.satelliteId+"", 59 - textRenderer.getWidth(pipe.satelliteId+"")/2, 31, 0x404040);
    }

    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/satellite.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }
}
