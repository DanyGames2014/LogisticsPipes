package net.danygames2014.logisticspipes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class SmallButtonWidget extends ButtonWidget {
    public SmallButtonWidget(int id, int x, int y, String text) {
        super(id, x, y, text);
    }

    public SmallButtonWidget(int id, int x, int y, int width, int height, String text) {
        super(id, x, y, width, height, text);
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }
        TextRenderer textRenderer = minecraft.textRenderer;
        GL11.glBindTexture(3553, minecraft.textureManager.getTextureId("/gui/gui.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean flag = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        int k = this.getYImage(flag);
        drawTexture(x, y, 0, 46 + k * 20, width / 2, height / 2);
        drawTexture(x + width / 2, y, 200 - width / 2, 46 + k * 20, width / 2, height / 2);

        drawTexture(x, y + height / 2, 0, 46 + 25 - height + k * 20, width / 2, height / 2);
        drawTexture(x + width / 2, y + height / 2, 200 - width / 2, 46 + 25 - height + k * 20, width / 2, height / 2);

        this.renderBackground(minecraft, mouseX, mouseY);
        if (!active) {
            drawCenteredTextWithShadow(textRenderer, this.text, x + width / 2, y + (height - 8) / 2, 0xffa0a0a0);
        } else if (flag) {
            drawCenteredTextWithShadow(textRenderer, this.text, x + width / 2, y + (height - 8) / 2, 0xffffa0);
        } else {
            drawCenteredTextWithShadow(textRenderer, this.text, x + width / 2, y + (height - 8) / 2, 0xe0e0e0);
        }
    }
}
