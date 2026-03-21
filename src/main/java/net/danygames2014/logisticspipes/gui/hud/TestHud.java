package net.danygames2014.logisticspipes.gui.hud;

import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.danygames2014.logisticspipes.util.gui.hud.BasicHUDButton;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class TestHud extends BasicGuiHud{

    public TestHud(){
        this.buttons.add(new BasicHUDButton("this is a button", -40, -30, 80, 10) {
            @Override
            public void clicked() {
                System.out.println("clicked");
            }

            @Override
            public boolean shouldRenderButton() {
                return true;
            }

            @Override
            public boolean buttonEnabled() {
                return true;
            }
        });
    }

    @Override
    public boolean display() {
        return true;
    }

    @Override
    public boolean cursorOnWindow(int x, int y) {
        return -50 < x && x < 50 && -50 < y && y < 50;
    }

    @Override
    public void renderHeadUpDisplay(double d, boolean day, Minecraft mc) {
        if(day) {
            GL11.glColor4b((byte)64, (byte)64, (byte)64, (byte)64);
        } else {
            GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)64);
        }
        BasicGuiHelper.drawGuiBackGround(mc, -50, -50, 50, 50, 0, false);
        if(day) {
            GL11.glColor4b((byte)64, (byte)64, (byte)64, (byte)127);
        } else {
            GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)127);
        }

        GL11.glTranslatef(0.0F, 0.0F, -0.01F);
        super.renderHeadUpDisplay(d, day, mc);

        GL11.glScalef(1.5F, 1.5F, 0.0001F);
        String message = "test screen";

        mc.textRenderer.drawWithShadow(message, -30, -30, 0xFFFFFF);
    }
}
