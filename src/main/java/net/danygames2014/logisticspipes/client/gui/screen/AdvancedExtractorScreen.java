package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.module.AdvancedExtractorModule;
import net.danygames2014.logisticspipes.screen.DummyScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

public class AdvancedExtractorScreen extends ScreenWithPrevious {
    private final Inventory playerInventory;
    private final AdvancedExtractorModule advancedExtractor;
    private final int slot;

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new ButtonWidget(0, width / 2 + 20, height / 2 - 34, 60, 20, advancedExtractor.areItemsIncluded() ? "Included" : "Excluded"));
        buttons.add(new ButtonWidget(1, width / 2 - 25, height / 2 - 34, 40, 20, "Sneaky"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            advancedExtractor.setItemsIncluded(!advancedExtractor.areItemsIncluded());
            ((ButtonWidget) buttons.get(0)).text = advancedExtractor.areItemsIncluded() ? "Included" : "Excluded";
//            PacketDispatcher.sendPacketToServer(new PacketPipeInteger(NetworkConstants.ADVANCED_EXTRACTOR_MODULE_INCLUDED_SET, pipe.xCoord, pipe.yCoord, pipe.zCoord, (_advancedExtractor.areItemsIncluded() ? 1 : 0) + (slot * 10)).getPacket());
        }
        if (button.id == 1) {
            // TODO: change sneaky direction
        }
    }

    public AdvancedExtractorScreen(Inventory playerInventory, LogisticPipeBlockEntity pipe, AdvancedExtractorModule advancedExtractor, Screen previousScreen, int slot) {
        super(null, pipe, previousScreen);
        this.advancedExtractor = advancedExtractor;
        this.slot = slot;
        DummyScreenHandler dummy = new DummyScreenHandler(playerInventory, advancedExtractor.getFilterInventory());
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
        textRenderer.draw(advancedExtractor.getFilterInventory().getName(), 8, 6, 0x404040);
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
