package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.SupplierLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.network.RequestPartialToggleC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.SupplierScreenHandler;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;

public class SupplierScreen extends LogisticsBaseScreen {
    private final Inventory dummyInventory;
    SupplierLogisticPipeBlockEntity pipe;

    public SupplierScreen(PlayerEntity player, SupplierLogisticPipeBlockEntity pipe) {
        super(player, pipe, new SupplierScreenHandler(player, pipe, pipe.getFilterInventory()));
        this.pipe = pipe;
        this.dummyInventory = pipe.getFilterInventory();
        this.backgroundWidth = 194;
        this.backgroundHeight = 186;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new ButtonWidget(0, width / 2 + 45, height / 2 - 25, 30, 20, pipe.isRequestingPartials() ? "Yes" : "No"));
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

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            pipe.setRequestingPartials(!pipe.isRequestingPartials());
            if (player.world.isRemote) {
                PacketHelper.send(new RequestPartialToggleC2SPacket(pipe.isRequestingPartials()));
            }
            ((ButtonWidget) buttons.get(0)).text = pipe.isRequestingPartials() ? "Yes" : "No";
        }
        super.buttonClicked(button);
    }
}
