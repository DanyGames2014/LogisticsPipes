package net.danygames2014.logisticspipes.gui.hud.modules;

import net.danygames2014.logisticspipes.interfaces.HUDButton;
import net.danygames2014.logisticspipes.interfaces.HUDModuleRenderer;
import net.danygames2014.logisticspipes.module.AdvancedExtractorModule;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.SneakyDirection;
import net.danygames2014.logisticspipes.util.gui.BasicGuiHelper;
import net.danygames2014.logisticspipes.util.gui.hud.BasicHUDButton;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class AdvancedExtractorHud implements HUDModuleRenderer {
    private List<HUDButton> buttons = new ArrayList<>();
    private int selected = 0;

    private AdvancedExtractorModule module;

    public AdvancedExtractorHud(AdvancedExtractorModule module) {
        this.module = module;
        this.buttons.add(new TabButton("Side",0,-30,-50,25,10));
        this.buttons.add(new TabButton("Inv",1,-5,-50,25,10));
    }

    @Override
    public void renderContent() {
        if(selected == 0) {
            Minecraft mc = Minecraft.INSTANCE;
            SneakyDirection d = module.getSneakyDirection();
            mc.textRenderer.draw("Extract" , -22, -22, 0);
            mc.textRenderer.draw("from:" , -22, -9, 0);
            mc.textRenderer.draw(d.name() , -22, 18, 0);
        } else {
            Minecraft mc = Minecraft.INSTANCE;
            GL11.glScalef(1.0F, 1.0F, -0.00001F);
            BasicGuiHelper.renderItemIdentifierStackListIntoHud(ItemIdentifierStack.getListFromInventory(module.getFilterInventory()), null, 0, -25, -32, 3, 9, 18, 18, mc, false, false, true, true);
            GL11.glScalef(1.0F, 1.0F, 1 / -0.00001F);
            if(module.areItemsIncluded()) {
                mc.textRenderer.draw("Included" , -22, 25, 0);
            } else {
                mc.textRenderer.draw("Excluded" , -22, 25, 0);
            }
        }
    }

    @Override
    public List<HUDButton> getButtons() {
        return buttons;
    }

    private class TabButton extends BasicHUDButton {
        private final int mode;

        public TabButton(String name, int mode, int x, int y, int width, int heigth) {
            super(name, x, y, width, heigth);
            this.mode = mode;
        }

        @Override
        public void clicked() {
            selected = mode;
        }

        @Override
        public void renderButton(boolean hover, boolean clicked) {
            GL11.glTranslatef(0.0F, 0.0F, -0.000005F);
            Minecraft mc = Minecraft.INSTANCE;
            if(hover) {
                GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)127);
                if(!clicked) {
                    GL11.glTranslatef(0.0F, 0.0F, -0.01F);
                }
            } else {
                GL11.glColor4b((byte)127, (byte)127, (byte)127, (byte)64);
            }
            GL11.glScaled(0.5D, 0.5D, 1.0D);
            BasicGuiHelper.drawGuiBackGround(mc, posX * 2, posY * 2, (posX + sizeX) * 2, (posY + sizeY) * 2 + 15, 0, false, true, true, false, true);
            GL11.glScaled(2.0D, 2.0D, 1.0D);

            if(clicked) {
                GL11.glTranslatef(0.0F, 0.0F, -0.01F);
            }

            GL11.glTranslatef(0.0F, 0.0F, -0.000005F);
            int color = 0;
            if(hover && !clicked) {
                color = 0xffffa0;
            } else if(!clicked) {
                color = 0x000000;
            } else  {
                color = 0x808080;
            }
            GL11.glScaled(0.8D, 0.8D, 1.0D);
            mc.textRenderer.draw(label ,(int) ((-(mc.textRenderer.getWidth(label) / (2* (1/0.8D))) + posX + sizeX / 2) * (1/0.8D)),(int) ((posY + (sizeY - 8) / 2) * (1/0.8D)) + 2, color);
            GL11.glScaled(1/0.8D, 1/0.8D, 1.0D);
            if(hover) {
                GL11.glTranslatef(0.0F, 0.0F, 0.01F);
            }
            GL11.glTranslatef(0.0F, 0.0F, 0.00001F);
        }

        @Override
        public boolean shouldRenderButton() {
            return true;
        }

        @Override
        public boolean buttonEnabled() {
            return mode != selected;
        }
    }
}
