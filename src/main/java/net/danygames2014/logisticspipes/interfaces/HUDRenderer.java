package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.client.Minecraft;

public interface HUDRenderer {
    void renderHeadUpDisplay(double d, boolean day, Minecraft mc);
    boolean display();
    boolean cursorOnWindow(int x, int y);
    void handleCursor(int x, int y);
}
