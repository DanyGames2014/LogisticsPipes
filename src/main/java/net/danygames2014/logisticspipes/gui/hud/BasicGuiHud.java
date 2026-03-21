package net.danygames2014.logisticspipes.gui.hud;

import net.danygames2014.logisticspipes.interfaces.HUDButton;
import net.danygames2014.logisticspipes.interfaces.HUDRenderer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicGuiHud implements HUDRenderer {
    protected final List<HUDButton> buttons = new ArrayList<>();

    @Override
    public void renderHeadUpDisplay(double d, boolean day, Minecraft mc) {
        for(HUDButton button:buttons) {
            GL11.glPushMatrix();
            button.renderAlways();
            if(button.shouldRenderButton()) {
                button.renderButton(button.isFocused(), button.isblockFocused());
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public void handleCursor(int x, int y) {
        GL11.glPushMatrix();
        for(HUDButton button:buttons) {
            if(!button.buttonEnabled() || !button.shouldRenderButton()) continue;
            if((button.getX() - 1 < x && x < (button.getX() + button.sizeX() + 1)) && (button.getY() - 1 < y && y < (button.getY() + button.sizeY() + 1))) {
                if(!button.isFocused() && !button.isblockFocused()) {
                    button.setFocused();
                } else if(button.focusedTime() > 400 && !button.isblockFocused()) {
                    button.clicked();
                    button.blockFocused();
                }
            } else if(button.isFocused() || button.isblockFocused()) {
                button.clearFocused();
            }
        }
        GL11.glPopMatrix();
    }
}
