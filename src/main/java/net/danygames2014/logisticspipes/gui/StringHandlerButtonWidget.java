package net.danygames2014.logisticspipes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;

public class StringHandlerButtonWidget extends ButtonWidget {

    private final StringHandler handler;

    public StringHandlerButtonWidget(int id, int x, int y, StringHandler handler) {
        super(id, x, y, "");
        this.handler = handler;
    }

    public StringHandlerButtonWidget(int id, int x, int y, int width, int height, StringHandler handler) {
        super(id, x, y, width, height, "");
        this.handler = handler;
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int mouseY) {
        this.text = handler.getContent();
        super.render(minecraft, mouseX, mouseY);
    }

    public interface StringHandler {
        String getContent();
    }
}
