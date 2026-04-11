package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.SupplierLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.screen.DummyScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

public class SupplierScreen extends HandledScreen {
    private Inventory playerInventory;
    private Inventory dummyInventory;
    SupplierLogisticPipeBlockEntity pipe;

    public SupplierScreen(Inventory playerInventory, Inventory dummyInventory, SupplierLogisticPipeBlockEntity pipe) {
        super(null);

        DummyScreenHandler dummy = new DummyScreenHandler(playerInventory, dummyInventory);
        dummy.addNormalSlotsForPlayerInventory(18, 97);

        int xOffset = 72;
        int yOffset = 18;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                dummy.addDummySlot(column + row * 3, xOffset + column * 18, yOffset + row * 18);
            }
        }

        this.handler = dummy;

        this.playerInventory = playerInventory;
        this.dummyInventory = dummyInventory;
        this.pipe = pipe;
        this.backgroundWidth = 194;
        this.backgroundHeight = 186;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw(dummyInventory.getName(), backgroundWidth / 2 - textRenderer.getWidth(dummyInventory.getName()) / 2, 6, 0x404040);
        textRenderer.draw("Inventory", 18, backgroundHeight - 102, 0x404040);
        textRenderer.draw("Partial requests:", backgroundWidth - 140, backgroundHeight - 112, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/supplier.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new ButtonWidget(0, width / 2 + 45, height / 2 - 25, 30, 20, pipe.isRequestingPartials() ? "Yes" : "No"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            pipe.setRequestingPartials(!pipe.isRequestingPartials());
            ((ButtonWidget) buttons.get(0)).text = pipe.isRequestingPartials() ? "Yes" : "No";
        }
        super.buttonClicked(button);
    }
}
