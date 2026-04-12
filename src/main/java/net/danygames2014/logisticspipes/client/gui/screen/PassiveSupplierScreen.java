package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.module.PassiveSupplierModule;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.PassiveSupplierScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class PassiveSupplierScreen extends ScreenWithPrevious {
    private final PassiveSupplierModule supplier;

    public PassiveSupplierScreen(PlayerEntity player, LogisticPipeBlockEntity pipe, PassiveSupplierModule supplier, Screen previousScreen) {
        super(player, pipe, new PassiveSupplierScreenHandler(player, supplier.getFilterInventory()), previousScreen);
        this.supplier = supplier;
        this.backgroundWidth = 175;
        this.backgroundHeight = 142;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw(supplier.getFilterInventory().getName(), 8, 6, 0x404040);
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
