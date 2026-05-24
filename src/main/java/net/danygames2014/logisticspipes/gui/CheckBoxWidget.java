package net.danygames2014.logisticspipes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class CheckBoxWidget extends ButtonWidget {
    private boolean state = false;

    public CheckBoxWidget(int id, int x, int y, int width, int height, boolean state) {
        super(id, x, y, width, height, "");
        this.state = state;
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int mouseY) {
        if(this.visible) {
            boolean var5 = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int var6 = this.getYImage(var5);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.textureManager.getTextureId("/assets/logisticspipes/stationapi/textures/gui/widget/checkbox_" + (state?"checked":"unchecked")  + (var6 == 2?"_hover":"") + ".png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.INSTANCE;
            tessellator.startQuads();
            tessellator.vertex(x			, y + height	, zOffset, 0	, 1);
            tessellator.vertex(x + width	, y + height	, zOffset, 1	, 1);
            tessellator.vertex(x + width	, y				, zOffset, 1	, 0);
            tessellator.vertex(x			, y				, zOffset, 0	, 0);
            tessellator.draw();
            isMouseOver(minecraft, mouseX, mouseY);
        }
    }

    public boolean change() {
        return  state = !state;
    }

    public boolean getState() {
        return state;
    }
}
