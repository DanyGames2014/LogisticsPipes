package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.gui.RenderSlot;
import net.danygames2014.logisticspipes.gui.SubScreen;
import net.danygames2014.logisticspipes.gui.SubScreenController;
import net.danygames2014.logisticspipes.screen.DummyScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;
import java.util.List;

public class LogisticsBaseScreen extends HandledScreen implements SubScreenController {
    public enum Colors
    {
        White,
        Black,
        LightGrey,
        MiddleGrey,
        DarkGrey,
        Red
    }

    protected int right;
    protected int bottom;
    protected int xCenter;
    protected int yCenter;
    protected final int xCenterOffset;
    protected final int yCenterOffset;

    private SubScreen subScreen;
    protected List<RenderSlot> slots = new ArrayList<>();

    public LogisticsBaseScreen(int xSize, int ySize, int xCenterOffset, int yCenterOffset){
        this(new DummyScreenHandler(null, null), xSize, ySize, xCenterOffset, yCenterOffset);
    }

    public LogisticsBaseScreen(ScreenHandler container){
        super(container);
        this.xCenterOffset = 0;
        this.yCenterOffset = 0;
    }

    public LogisticsBaseScreen(ScreenHandler container, int xSize, int ySize, int xCenterOffset, int yCenterOffset){
        super(container);
        this.backgroundWidth = xSize;
        this.backgroundHeight = ySize;
        this.xCenterOffset = xCenterOffset;
        this.yCenterOffset = yCenterOffset;
    }

    @Override
    public void init() {
        super.init();
//        this.guiLeft =  width/2 - backgroundWidth/2 + xCenterOffset;
//        this.guiTop = height/2 - backgroundHeight/2  + yCenterOffset;

        this.right = width/2 + backgroundWidth/2 + xCenterOffset;
        this.bottom = height/2 + backgroundHeight/2 + yCenterOffset;

//        this.xCenter = (right + guiLeft) / 2;
//        this.yCenter = (bottom + guiTop) / 2;
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
        return null;
    }

    @Override
    public void setSubScreen(SubScreen subScreen) {
        if(this.subScreen == null) {
            this.subScreen = subScreen;
            this.subScreen.init(this.minecraft, this.width, this.height);
            this.subScreen.register(this);
            this.subScreen.init();
        }
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        if(subScreen != null) {
            subScreen.init(minecraft, width, height);
        }
    }

    @Override
    public void resetSubScreen() {
        subScreen = null;
    }

    @Override
    public void renderBackground() {
        if(subScreen == null){
            super.renderBackground();
        }
    }

    @Override
    protected void drawBackground(float tickDelta) {

    }
}
