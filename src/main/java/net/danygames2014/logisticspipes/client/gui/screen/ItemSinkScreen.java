package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.StringHandlerButtonWidget;
import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.danygames2014.logisticspipes.screen.handler.ItemSinkScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

public class ItemSinkScreen extends ScreenWithPrevious {
    private final ItemSinkModule itemSink;
    private final int slot;

    public ItemSinkScreen(PlayerEntity player, LogisticPipeBlockEntity pipe, ItemSinkModule itemSink, Screen previousScreen, int slot) {
        super(player, pipe, new ItemSinkScreenHandler(player, itemSink.getFilterInventory()), previousScreen);
        this.itemSink = itemSink;
        this.slot = slot;
        this.backgroundWidth = 175;
        this.backgroundHeight = 142;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();

        buttons.clear();
        buttons.add(new StringHandlerButtonWidget(0, width / 2 + 50, height / 2 - 34, 30, 20, this::getContent));
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw(itemSink.getFilterInventory().getName(), 8, 6, 0x404040);
        textRenderer.draw("Inventory", 8, backgroundHeight - 92, 0x404040);
        textRenderer.draw("Default route:", 65, 45, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/itemsink.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }

    private String getContent() {
        return itemSink.isDefaultRoute() ? "Yes" : "No";
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            itemSink.setDefaultRoute(!itemSink.isDefaultRoute());
            // TODO: send packet to server
        }
    }
}
