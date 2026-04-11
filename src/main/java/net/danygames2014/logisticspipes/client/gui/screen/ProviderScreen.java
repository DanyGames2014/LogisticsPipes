package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.StringHandlerButtonWidget;
import net.danygames2014.logisticspipes.module.ProviderModule;
import net.danygames2014.logisticspipes.screen.DummyScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

public class ProviderScreen extends ScreenWithPrevious {
    private final Inventory playerInventory;
    private final ProviderModule provider;
    private final LogisticPipeBlockEntity pipe;
    private final int slot;

    public ProviderScreen(Inventory playerInventory, LogisticPipeBlockEntity pipe, ProviderModule provider, Screen previousScreen, int slot) {
        super(null, pipe, previousScreen);
        this.playerInventory = playerInventory;
        this.provider = provider;
        this.pipe = pipe;
        this.slot = slot;

        DummyScreenHandler dummy = new DummyScreenHandler(playerInventory, provider.getFilterInventory());
        dummy.addNormalSlotsForPlayerInventory(18, 97);

        int xOffset = 72;
        int yOffset = 18;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                dummy.addDummySlot(column + row * 3, xOffset + column * 18, yOffset + row * 18);
            }
        }

        this.handler = dummy;
        backgroundWidth = 194;
        backgroundHeight = 186;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        buttons.clear();
        buttons.add(new StringHandlerButtonWidget(0, width / 2 + 40, height / 2 - 59, 45, 20, this::getContent));
        buttons.add(new ButtonWidget(1, width / 2 - 90, height / 2 - 41, 38, 20, "Switch"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            provider.setFilterExcluded(!provider.isExcludeFilter());
        } else if (button.id == 1) {
            provider.nextExtractionMode();
        }
        super.buttonClicked(button);
    }

    private String getExtractionModeString() {
        return switch (provider.getExtractionMode()) {
            case Normal -> "Normal";
            case LeaveFirst -> "Leave 1st stack";
            case LeaveLast -> "Leave last stack";
            case LeaveFirstAndLast -> "Leave first & last stack";
            case Leave1PerStack -> "Leave 1 item per stack";
        };
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
    protected void drawForeground() {
        super.drawForeground();
        textRenderer.draw(provider.getFilterInventory().getName(), backgroundWidth / 2 - textRenderer.getWidth(provider.getFilterInventory().getName()) / 2, 6, 0x404040);
        textRenderer.draw("Inventory", 18, backgroundHeight - 102, 0x404040);
        textRenderer.draw("Mode: " + getExtractionModeString(), 9, backgroundHeight - 112, 0x404040);
    }

    String getContent() {
        return provider.isExcludeFilter() ? "Exclude" : "Include";
    }
}
