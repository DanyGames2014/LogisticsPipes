package net.danygames2014.logisticspipes.gui;

import net.danygames2014.logisticspipes.client.gui.screen.LogisticsBaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.opengl.GL11;

public class SubScreen extends Screen implements SubScreenController {
    protected int guiLeft;
    protected int guiTop;
    protected int xCenter;
    protected int yCenter;
    protected int right;
    protected int bottom;
    protected int xSize;
    protected int ySize;
    protected int xCenterOffset;
    protected int yCenterOffset;

    private SubScreen subScreen;

    protected SubScreenController controller;

    public SubScreen(int xSize, int ySize, int xOffset, int yOffset) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.xCenterOffset = xOffset;
        this.yCenterOffset = yOffset;
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = width / 2 - xSize / 2 + xCenterOffset;
        this.guiTop = height / 2 - ySize / 2 + yCenterOffset;

        this.right = width / 2 + xSize / 2 + xCenterOffset;
        this.bottom = height / 2 + ySize / 2 + yCenterOffset;

        this.xCenter = (right + guiLeft) / 2;
        this.yCenter = (bottom + guiTop) / 2;
    }

    public void register(SubScreenController gui) {
        controller = gui;
    }

    public void exitScreen() {
        controller.resetSubScreen();
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (character == 1) {
            exitScreen();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if (subScreen != null) {
            GL11.glTranslatef(0.0F, 0.0F, 1.0F);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            if (!subScreen.hasSubScreen()) {
                super.renderBackground();
            }
            subScreen.render(mouseX, mouseY, delta);
            GL11.glTranslatef(0.0F, 0.0F, -1.0F);
        }
    }

    @Override
    public void onMouseEvent() {
        if (subScreen != null) {
            subScreen.onMouseEvent();
        }
        this.onMouseEventSub();
    }

    public void onMouseEventSub() {
        super.onMouseEvent();
    }

    @Override
    public void onKeyboardEvent() {
        if (subScreen != null) {
            subScreen.onKeyboardEvent();
        } else {
            super.onKeyboardEvent();
        }
    }

    @Override
    public void setSubScreen(SubScreen subScreen) {
        if (this.subScreen == null) {
            this.subScreen = subScreen;
            this.subScreen.register(this);
            this.subScreen.init(this.minecraft, this.width, this.height);
            this.subScreen.init();
        }
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        if (subScreen != null) {
            subScreen.init(minecraft, width, height);
        }
    }

    @Override
    public void resetSubScreen() {
        subScreen = null;
    }

    @Override
    public boolean hasSubScreen() {
        return subScreen != null;
    }

    @Override
    public SubScreen getSubScreen() {
        return subScreen;
    }

    @Override
    public LogisticsBaseScreen getBaseScreen() {
        return controller.getBaseScreen();
    }
}
