package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.CraftingLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.SmallButtonWidget;
import net.danygames2014.logisticspipes.screen.handler.CraftingPipeScreenHandler;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.opengl.GL11;

public class CraftingPipeScreen extends LogisticsBaseScreen {
    private final CraftingLogisticPipeBlockEntity pipe;
    private final ButtonWidget[] buttonArray;

    public CraftingPipeScreen(PlayerEntity player, CraftingLogisticPipeBlockEntity pipe) {
        super(player, pipe, new CraftingPipeScreenHandler(player, pipe));
        this.pipe = pipe;
        this.backgroundWidth = 195;
        this.backgroundHeight = 187;
        buttonArray = new ButtonWidget[6];
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();
        buttons.add(new SmallButtonWidget(0, (width - backgroundWidth) / 2 + 164, (height - backgroundHeight) / 2 + 50, 10, 10, ">"));
        buttons.add(new SmallButtonWidget(1, (width - backgroundWidth) / 2 + 129, (height - backgroundHeight) / 2 + 50, 10, 10, "<"));
        buttons.add(new SmallButtonWidget(3, (width - backgroundWidth) / 2 + 47, (height - backgroundHeight) / 2 + 50, 37, 10, "Import"));
        buttons.add(new SmallButtonWidget(4, (width - backgroundWidth) / 2 + 15, (height - backgroundHeight) / 2 + 50, 28, 10, "Open"));

        for (int i = 0; i < 6; i++) {
            buttons.add(buttonArray[i] = new SmallButtonWidget(5 + i, (width - backgroundWidth) / 2 + 20 + 18 * i, (height - backgroundHeight) / 2 + 37, 10, 10, ">"));
            buttonArray[i].visible = false;
        }

        buttons.add(new SmallButtonWidget(20, (width - backgroundWidth) / 2 + 164, (height - backgroundHeight) / 2 + 85, 10, 10, ">"));
        buttons.add(new SmallButtonWidget(21, (width - backgroundWidth) / 2 + 129, (height - backgroundHeight) / 2 + 85, 10, 10, "<"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (5 <= button.id && button.id < 11) {
            pipe.handleStackMove(button.id - 5);
        }
        
        switch (button.id) {
            case 0:
                pipe.setNextSatellite(player);
                return;
            case 1:
                pipe.setPrevSatellite(player);
                return;
            case 2:
//                pipe.paintPathToSatellite();
                return;
            case 3:
                pipe.importFromCraftingTable(player);
                return;
            case 4:
                pipe.openAttachedGui(player);
                return;
            case 20:
                pipe.priorityUp(player);
                return;
            case 21:
                pipe.priorityDown(player);
                return;
            default:
                super.buttonClicked(button);
                return;
        }
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Inputs", 18, 7, 0x404040);
        textRenderer.draw("Output", 48, 67, 0x404040);
        textRenderer.draw("Inventory", 18, 86, 0x404040);
        textRenderer.draw("Satellite", 132, 7, 0x404040);

        if (pipe.satelliteId == 0) {
            textRenderer.draw("Off", 144, 52, 0x404040);
        } else {
            textRenderer.draw("" + pipe.satelliteId, 155 - textRenderer.getWidth("" + pipe.satelliteId), 52, 0x404040);
        }
        
        textRenderer.draw("Priority:", 132, 75, 0x404040);
        textRenderer.draw("" + pipe.priority, 152 - (textRenderer.getWidth("" + pipe.priority) / 2), 87, 0x404040);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/crafting.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int startX = (this.width - this.backgroundWidth) / 2;
        int startY = (this.height - this.backgroundHeight) / 2;
        drawTexture(startX, startY, 0, 0, backgroundWidth, backgroundHeight);

        fill(400, 400, 0, 0, 0x404040);

        for (int count = 36; count < 42; count++) {
            Slot slot = handler.getSlot(count);
            if (slot != null && slot.getStack() != null && slot.getStack().getMaxCount() < 2) {
                fill(guiLeft + 18 + (18 * (count - 36)), guiTop + 18, guiLeft + 18 + (18 * (count - 36)) + 16, guiTop + 18 + 16, 0xFFFF0000);
                buttonArray[count - 36].visible = true;
            } else {
                buttonArray[count - 36].visible = false;
            }
        }
    }
}
