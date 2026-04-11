package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.module.TerminusModule;
import net.danygames2014.logisticspipes.screen.DummyScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

public class TerminusScreen extends ScreenWithPrevious {
    private final Inventory playerInventory;
    private final TerminusModule terminus;

    public TerminusScreen(Inventory playerInventory, LogisticPipeBlockEntity pipe, TerminusModule terminus, Screen previousScreen) {
        super(null, pipe, previousScreen);
        this.terminus = terminus;
        DummyScreenHandler dummy = new DummyScreenHandler(playerInventory, terminus.getFilterInventory());
        dummy.addNormalSlotsForPlayerInventory(8, 60);

        //Pipe slots
        for (int pipeSlot = 0; pipeSlot < 9; pipeSlot++) {
            dummy.addDummySlot(pipeSlot, 8 + pipeSlot * 18, 18);
        }

        this.handler = dummy;
        this.playerInventory = playerInventory;
        this.backgroundWidth = 175;
        this.backgroundHeight = 142;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw(terminus.getFilterInventory().getName(), 8, 6, 0x404040);
        textRenderer.draw("Inventory", 8, backgroundHeight - 92, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/itemsink.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }
}
