package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.gui.*;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class LogisticsBaseScreen extends HandledScreen implements SubScreenController {
    public enum Colors {
        White,
        Black,
        LightGrey,
        MiddleGrey,
        DarkGrey,
        Red
    }

    protected int guiLeft;
    protected int guiTop;
    protected int right;
    protected int bottom;
    protected int xCenter;
    protected int yCenter;
    protected final int xCenterOffset;
    protected final int yCenterOffset;

    private SubScreen subScreen;
    protected List<RenderSlot> slots = new ArrayList<>();
    
    public PlayerEntity player;
    public Inventory playerInventory;
    public LogisticPipeBlockEntity blockEntity;
    public ModuleScreenHandler moduleHandler;

//    public LogisticsBaseScreen(int xSize, int ySize, int xCenterOffset, int yCenterOffset) {
//        this(new ModuleScreenHandler(null, null), xSize, ySize, xCenterOffset, yCenterOffset);
//    }

    public LogisticsBaseScreen(PlayerEntity player, LogisticPipeBlockEntity blockEntity, ModuleScreenHandler handler) {
        super(handler);
        
        this.player = player;
        this.playerInventory = player.inventory;
        this.blockEntity = blockEntity;
        this.moduleHandler = handler;
        this.handler = handler;
        
        this.xCenterOffset = 0;
        this.yCenterOffset = 0;
    }

//    public LogisticsBaseScreen(ScreenHandler container, int xSize, int ySize, int xCenterOffset, int yCenterOffset) {
//        super(container);
//        this.backgroundWidth = xSize;
//        this.backgroundHeight = ySize;
//        this.xCenterOffset = xCenterOffset;
//        this.yCenterOffset = yCenterOffset;
//    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = width / 2 - backgroundWidth / 2 + xCenterOffset;
        this.guiTop = height / 2 - backgroundHeight / 2 + yCenterOffset;

        this.right = width / 2 + backgroundWidth / 2 + xCenterOffset;
        this.bottom = height / 2 + backgroundHeight / 2 + yCenterOffset;

        this.xCenter = (right + guiLeft) / 2;
        this.yCenter = (bottom + guiTop) / 2;
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
        if (this.subScreen == null) {
            this.subScreen = subScreen;
            this.subScreen.init(this.minecraft, this.width, this.height);
            this.subScreen.register(this);
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
    public void renderBackground() {
        if (subScreen == null) {
            super.renderBackground();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (subScreen != null) {
            // There is some weird reflection code here in the original but I'm leaving it out for now
            super.render(0, 0, delta);
            Lighting.turnOff();
            GL11.glTranslatef(0.0F, 0.0F, 101.0F);
            GL11.glTranslatef(0.0F, 0.0F, 101.0F);
            if (!subScreen.hasSubScreen()) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                super.renderBackground();
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            subScreen.render(mouseX, mouseY, delta);
            GL11.glTranslatef(0.0F, 0.0F, -101.0F);
            Lighting.turnOn();
        } else {
            super.render(mouseX, mouseY, delta);
            Lighting.turnOff();
            for (RenderSlot slot : slots) {
                int mouseXOffset = mouseX - guiLeft;
                int mouseYOffset = mouseY - guiTop;
                int mouseXMax = mouseXOffset - slot.getSize();
                int mouseYMax = mouseYOffset - slot.getSize();
                if (slot.getXPos() < mouseXOffset && slot.getXPos() > mouseXMax && slot.getYPos() < mouseYOffset && slot.getYPos() > mouseYMax) {
                    if (slot.displayToolTip()) {
                        if (slot.getToolTipText() != null && !slot.getToolTipText().isEmpty()) {
                            ArrayList<String> list = new ArrayList<>();
                            list.add(slot.getToolTipText());
                            BasicGuiHelper.drawToolTip(mouseX, mouseY, list, 0xf);
                        }
                    }
                }
            }
            Lighting.turnOn();
        }
    }


    @Override
    public void onMouseEvent() {
        if (subScreen != null) {
            subScreen.onMouseEvent();
        } else {
            this.onMouseEventSub();
        }
    }

    public void onMouseEventSub() {
        super.onMouseEvent();
    }


    @Override
    public void onKeyboardEvent() {
        if (subScreen != null) {
            subScreen.onKeyboardEvent();
        } else {
            this.onKeyboardEventSub();
        }
    }

    public void onKeyboardEventSub() {
        super.onKeyboardEvent();
    }

    public void addRenderSlot(RenderSlot slot) {
        this.slots.add(slot);
    }

    @Override
    protected void drawForeground() {
        super.drawForeground();
    }

    @Override
    protected void drawBackground(float tickDelta) {
        for (RenderSlot slot : slots) {
            if (slot instanceof SpriteRenderSlot spriteRenderSlot) {
                if (slot.drawSlotBackground()) {
                    BasicGuiHelper.drawSlotBackground(minecraft, slot.getXPos(), slot.getYPos());
                }
                if (spriteRenderSlot.drawSlotSprite() && !spriteRenderSlot.customRender(minecraft, zOffset)) {
                    BasicGuiHelper.renderSpriteAt(slot.getXPos() + 1, slot.getYPos() + 1, zOffset, spriteRenderSlot.getSprite());
                }
            } else if (slot instanceof SmallColorRenderSlot smallColorRenderSlot) {
                if (slot.drawSlotBackground()) {
                    BasicGuiHelper.drawSmallSlotBackground(minecraft, slot.getXPos(), slot.getYPos());
                }
                if (smallColorRenderSlot.drawColor()) {
                    fill(slot.getXPos() + 1, slot.getYPos() + 1, slot.getXPos() + 7, slot.getYPos() + 7, smallColorRenderSlot.getColor());
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
//        for (RenderSlot slot : slots) {
//            int mouseXOffset = mouseX - guiLeft;
//            int mouseYOffset = mouseY - guiTop;
//            int mouseXMax = mouseXOffset - slot.getSize();
//            int mouseYMax = mouseYOffset - slot.getSize();
//            if (slot.getXPos() < mouseXOffset && slot.getXPos() > mouseXMax && slot.getYPos() < mouseYOffset && slot.getYPos() > mouseYMax) {
//                slot.mouseClicked(button);
//                return;
//            }
//        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public void drawPoint(int x, int y, int color) {
        fill(x, y, x + 1, y + 1, color);
    }

    public void drawPoint(int x, int y, Colors color) {
        fill(x, y, x + 1, y + 1, BasicGuiHelper.ConvertEnumToColor(color));
    }

    public void fill(int x1, int y1, int x2, int y2, Colors color) {
        fill(x1, y1, x2, y2, BasicGuiHelper.ConvertEnumToColor(color));
    }

    public void drawLine(int x1, int y1, int x2, int y2, Colors color) {
        int lasty = y1;
        for (int dx = 0; x1 + dx < x2; dx++) {
            int plotx = x1 + dx;
            int ploty = y1 + (y2 - y1) / (x2 - x1 - 1) * dx;
            drawPoint(plotx, ploty, color);
            while (lasty < ploty) {
                drawPoint(plotx, ++lasty, color);
            }
            while (lasty > ploty) {
                drawPoint(plotx, --lasty, color);
            }
        }
        while (lasty < y2) {
            drawPoint(x2, ++lasty, color);
        }
        while (lasty > y2) {
            drawPoint(x2, --lasty, color);
        }
    }
}
