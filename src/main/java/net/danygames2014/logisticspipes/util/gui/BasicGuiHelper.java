package net.danygames2014.logisticspipes.util.gui;

import net.danygames2014.buildcraft.util.ScreenUtil;
import net.danygames2014.logisticspipes.client.gui.screen.LogisticsBaseScreen;
import net.danygames2014.logisticspipes.interfaces.ItemSearch;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class BasicGuiHelper {
    public static void drawGuiBackGround(Minecraft mc, int guiLeft, int guiTop, int right, int bottom, float zLevel, boolean resetColor) {
        drawGuiBackGround(mc, guiLeft, guiTop, right, bottom, zLevel, resetColor, true, true, true, true);
    }

    private static final String BACKGROUND = "/assets/logisticspipes/stationapi/textures/gui/gui_background.png";
    private static final String SLOT = "/assets/logisticspipes/stationapi/textures/gui/slot/slot.png";
    private static final String SMALL_SLOT = "/assets/logisticspipes/stationapi/textures/gui/slot/small_slot.png";
    private static final String BIG_SLOT = "/assets/logisticspipes/stationapi/textures/gui/slot/big_slot.png";

    public static void drawGuiBackGround(Minecraft mc, int guiLeft, int guiTop, int right, int bottom, float zLevel, boolean resetColor, boolean displayTop, boolean displayLeft, boolean displayBottom, boolean displayRight) {
        if (resetColor) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        mc.textureManager.bindTexture(mc.textureManager.getTextureId(BACKGROUND));

        if (displayTop) {
            //Top Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft + 15, guiTop + 15, zLevel, 0.33, 0.33);
            var9.vertex(right - 15, guiTop + 15, zLevel, 0.66, 0.33);
            var9.vertex(right - 15, guiTop, zLevel, 0.66, 0);
            var9.vertex(guiLeft + 15, guiTop, zLevel, 0.33, 0);
            var9.draw();
        }

        if (displayLeft) {
            //Left Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft, bottom - 15, zLevel, 0, 0.66);
            var9.vertex(guiLeft + 15, bottom - 15, zLevel, 0.33, 0.66);
            var9.vertex(guiLeft + 15, guiTop + 15, zLevel, 0.33, 0.33);
            var9.vertex(guiLeft, guiTop + 15, zLevel, 0, 0.33);
            var9.draw();
        }

        if (displayBottom) {
            //Bottom Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft + 15, bottom, zLevel, 0.33, 1);
            var9.vertex(right - 15, bottom, zLevel, 0.66, 1);
            var9.vertex(right - 15, bottom - 15, zLevel, 0.66, 0.66);
            var9.vertex(guiLeft + 15, bottom - 15, zLevel, 0.33, 0.66);
            var9.draw();
        }

        if (displayRight) {
            //Right Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(right - 15, bottom - 15, zLevel, 0.66, 0.66);
            var9.vertex(right, bottom - 15, zLevel, 1, 0.66);
            var9.vertex(right, guiTop + 15, zLevel, 1, 0.33);
            var9.vertex(right - 15, guiTop + 15, zLevel, 0.66, 0.33);
            var9.draw();
        }

        if (displayTop && displayLeft) {
            //Top Left
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft, guiTop + 15, zLevel, 0, 0.33);
            var9.vertex(guiLeft + 15, guiTop + 15, zLevel, 0.33, 0.33);
            var9.vertex(guiLeft + 15, guiTop, zLevel, 0.33, 0);
            var9.vertex(guiLeft, guiTop, zLevel, 0, 0);
            var9.draw();
        }

        if (displayBottom && displayLeft) {
            //Bottom Left
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft, bottom, zLevel, 0, 1);
            var9.vertex(guiLeft + 15, bottom, zLevel, 0.33, 1);
            var9.vertex(guiLeft + 15, bottom - 15, zLevel, 0.33, 0.66);
            var9.vertex(guiLeft, bottom - 15, zLevel, 0, 0.66);
            var9.draw();
        }

        if (displayBottom && displayRight) {
            //Bottom Right
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(right - 15, bottom, zLevel, 0.66, 1);
            var9.vertex(right, bottom, zLevel, 1, 1);
            var9.vertex(right, bottom - 15, zLevel, 1, 0.66);
            var9.vertex(right - 15, bottom - 15, zLevel, 0.66, 0.66);
            var9.draw();
        }

        if (displayTop && displayRight) {
            //Top Right
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(right - 15, guiTop + 15, zLevel, 0.66, 0.33);
            var9.vertex(right, guiTop + 15, zLevel, 1, 0.33);
            var9.vertex(right, guiTop, zLevel, 1, 0);
            var9.vertex(right - 15, guiTop, zLevel, 0.66, 0);
            var9.draw();
        }

        //Center
        Tessellator var9 = Tessellator.INSTANCE;
        var9.startQuads();
        var9.vertex(guiLeft + 15, bottom - 15, zLevel, 0.33, 0.66);
        var9.vertex(right - 15, bottom - 15, zLevel, 0.66, 0.66);
        var9.vertex(right - 15, guiTop + 15, zLevel, 0.66, 0.33);
        var9.vertex(guiLeft + 15, guiTop + 15, zLevel, 0.33, 0.33);
        var9.draw();
    }

    private static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6, float zOffset) {
        float var7 = (float) (par5 >> 24 & 255) / 255.0F;
        float var8 = (float) (par5 >> 16 & 255) / 255.0F;
        float var9 = (float) (par5 >> 8 & 255) / 255.0F;
        float var10 = (float) (par5 & 255) / 255.0F;
        float var11 = (float) (par6 >> 24 & 255) / 255.0F;
        float var12 = (float) (par6 >> 16 & 255) / 255.0F;
        float var13 = (float) (par6 >> 8 & 255) / 255.0F;
        float var14 = (float) (par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.color(var8, var9, var10, var7);
        tessellator.vertex(par3, par2, zOffset);
        tessellator.vertex(par1, par2, zOffset);
        tessellator.color(var12, var13, var14, var11);
        tessellator.vertex(par1, par4, zOffset);
        tessellator.vertex(par3, par4, zOffset);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void fill(int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int var6 = x1;
            x1 = x2;
            x2 = var6;
        }

        if (y1 < y2) {
            int var11 = y1;
            y1 = y2;
            y2 = var11;
        }

        float var12 = (float)(color >> 24 & 255) / 255.0F;
        float var7 = (float)(color >> 16 & 255) / 255.0F;
        float var8 = (float)(color >> 8 & 255) / 255.0F;
        float var9 = (float)(color & 255) / 255.0F;
        Tessellator var10 = Tessellator.INSTANCE;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(var7, var8, var9, var12);
        var10.startQuads();
        var10.vertex(x1, y2, 0.0F);
        var10.vertex(x2, y2, 0.0F);
        var10.vertex(x2, y1, 0.0F);
        var10.vertex(x1, y1, 0.0F);
        var10.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        float var7 = (float)(colorStart >> 24 & 255) / 255.0F;
        float var8 = (float)(colorStart >> 16 & 255) / 255.0F;
        float var9 = (float)(colorStart >> 8 & 255) / 255.0F;
        float var10 = (float)(colorStart & 255) / 255.0F;
        float var11 = (float)(colorEnd >> 24 & 255) / 255.0F;
        float var12 = (float)(colorEnd >> 16 & 255) / 255.0F;
        float var13 = (float)(colorEnd >> 8 & 255) / 255.0F;
        float var14 = (float)(colorEnd & 255) / 255.0F;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Tessellator var15 = Tessellator.INSTANCE;
        var15.startQuads();
        var15.color(var8, var9, var10, var7);
        var15.vertex((double)endX, (double)startY, (double)0.0F);
        var15.vertex((double)startX, (double)startY, (double)0.0F);
        var15.color(var12, var13, var14, var11);
        var15.vertex((double)startX, (double)endY, (double)0.0F);
        var15.vertex((double)endX, (double)endY, (double)0.0F);
        var15.draw();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(double par1, double par2, double par3, double par4, double par5, double par6, float zLevel) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.INSTANCE;
        var9.startQuads();
        var9.vertex(par1 + 0, par2 + par6, zLevel, (float) (par3 + 0) * var7, (float) (par4 + par6) * var8);
        var9.vertex(par1 + par5, par2 + par6, zLevel, (float) (par3 + par5) * var7, (float) (par4 + par6) * var8);
        var9.vertex(par1 + par5, par2 + 0, zLevel, (float) (par3 + par5) * var7, (float) (par4 + 0) * var8);
        var9.vertex(par1 + 0, par2 + 0, zLevel, (float) (par3 + 0) * var7, (float) (par4 + 0) * var8);
        var9.draw();
    }

    public static void renderSpriteAt(int x, int y, float zOffset, Atlas.Sprite sprite) {
        ScreenUtil.drawSprite(sprite, x, y, 16, 16, zOffset);
    }

    public static void drawPlayerInventoryBackground(Minecraft mc, int xOffset, int yOffset) {
        //Player "backpack"
        for(int row = 0; row < 3; row++) {
            for(int column = 0; column < 9; column++)
            {
                drawSlotBackground(mc, xOffset + column * 18 - 1, yOffset + row * 18 - 1);
            }
        }
        //Player "hotbar"
        for(int i1 = 0; i1 < 9; i1++) {
            drawSlotBackground(mc, xOffset + i1 * 18 - 1, yOffset + 58 - 1);
        }
    }

    public static void drawSlotBackground(Minecraft mc, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.textureManager.bindTexture(mc.textureManager.getTextureId(SLOT));

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + 18, 0, 0, 1);
        tessellator.vertex(x + 18, y + 18, 0, 1, 1);
        tessellator.vertex(x + 18, y, 0, 1, 0);
        tessellator.vertex(x, y, 0, 0, 0);
        tessellator.draw();
    }

    public static void drawSlotBackground(Minecraft mc, int x, int y, int color) {
        float colorA = (color >> 24 & 0xFF) / 255.0F;
        float colorR = (color >> 16 & 0xFF) / 255.0F;
        float colorG = (color >> 8 & 0xFF) / 255.0F;
        float colorB = (color & 0xFF) / 255.0F;
        GL11.glColor4f(colorR, colorG, colorB, colorA);
        mc.textureManager.bindTexture(mc.textureManager.getTextureId(SLOT));

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + 18, 0, 0, 1);
        tessellator.vertex(x + 18, y + 18, 0, 1, 1);
        tessellator.vertex(x + 18, y, 0, 1, 0);
        tessellator.vertex(x, y, 0, 0, 0);
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawSmallSlotBackground(Minecraft mc, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.textureManager.bindTexture(mc.textureManager.getTextureId(SMALL_SLOT));

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + 8, 0, 0, 1);
        tessellator.vertex(x + 8, y + 8, 0, 1, 1);
        tessellator.vertex(x + 8, y, 0, 1, 0);
        tessellator.vertex(x, y, 0, 0, 0);
        tessellator.draw();
    }

    public static void drawBigSlotBackground(Minecraft mc, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.textureManager.bindTexture(mc.textureManager.getTextureId(BIG_SLOT));

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + 26, 0, 0, 1);
        tessellator.vertex(x + 26, y + 26, 0, 1, 1);
        tessellator.vertex(x + 26, y, 0, 1, 0);
        tessellator.vertex(x, y, 0, 0, 0);
        tessellator.draw();
    }

    public static int ConvertEnumToColor(LogisticsBaseScreen.Colors color) {
        return switch (color) {
            case Black -> 0xFF000000;
            case White -> 0xFFFFFFFF;
            case DarkGrey -> 0xFF555555;
            case MiddleGrey -> 0xFF8b8b8b;
            case LightGrey -> 0xFFC6C6C6;
            case Red -> 0xFFFF0000;
        };
    }

    public static void drawToolTip(int posX, int posY, List<String> msg, int color) {

        if (!msg.isEmpty()) {
            int var10 = 0;
            int var11;
            int var12;

            for (var11 = 0; var11 < msg.size(); ++var11) {
                var12 = Minecraft.INSTANCE.textRenderer.getWidth(msg.get(var11));

                if (var12 > var10) {
                    var10 = var12;
                }
            }

            var11 = posX + 12;
            var12 = posY - 12;
            int var14 = 8;

            if (msg.size() > 1) {
                var14 += 2 + (msg.size() - 1) * 10;
            }

            GL11.glEnable(32826);
            GL11.glDisable(2896 /*GL_LIGHTING*/);
            GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
            float zOffset = 300.0F;
            int var15 = -267386864;
            fillGradient(var11 - 3, var12 - 4, var11 + var10 + 3, var12 + var14 + 4, 0xC0000000, 0xC0000000);
//            drawGradientRect(var11 - 3, var12 + var14 + 3, var11 + var10 + 3, var12 + var14 + 4, var15, var15, zOffset);
//            drawGradientRect(var11 - 3, var12 - 3, var11 + var10 + 3, var12 + var14 + 3, var15, var15, zOffset);
//            drawGradientRect(var11 - 4, var12 - 3, var11 - 3, var12 + var14 + 3, var15, var15, zOffset);
//            drawGradientRect(var11 + var10 + 3, var12 - 3, var11 + var10 + 4, var12 + var14 + 3, var15, var15, zOffset);
//            int var16 = 1347420415;
//            int var17 = (var16 & 16711422) >> 1 | var16 & -16777216;
//            drawGradientRect(var11 - 3, var12 - 3 + 1, var11 - 3 + 1, var12 + var14 + 3 - 1, var16, var17, zOffset);
//            drawGradientRect(var11 + var10 + 2, var12 - 3 + 1, var11 + var10 + 3, var12 + var14 + 3 - 1, var16, var17, zOffset);
//            drawGradientRect(var11 - 3, var12 - 3, var11 + var10 + 3, var12 - 3 + 1, var16, var16, zOffset);
//            drawGradientRect(var11 - 3, var12 + var14 + 2, var11 + var10 + 3, var12 + var14 + 3, var17, var17, zOffset);

            for (int var18 = 0; var18 < msg.size(); ++var18) {
                String var19 = msg.get(var18);

                if(var18 != 0) {
                    var19 = "§f" + var19;
                }

                Minecraft.INSTANCE.textRenderer.drawWithShadow(var19, var11, var12, var18 == 0 ? color : -1);

                if (var18 == 0) {
                    var12 += 2;
                }

                var12 += 10;
            }


            GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
            GL11.glEnable(2896 /*GL_LIGHTING*/);
            GL11.glDisable(32826);
        }
    }

    public static void renderItemIdentifierStackListIntoGui(List<ItemIdentifierStack> _allItems, ItemSearch itemSearch, int page, int left , int top, int columns, int items, int xSize, int ySize, Minecraft mc, boolean displayAmount, boolean forcenumber) {
        renderItemIdentifierStackListIntoGui(_allItems, itemSearch, page, left, top, columns, items, xSize, ySize, mc, displayAmount, forcenumber, true);
    }

    public static void renderItemIdentifierStackListIntoGui(List<ItemIdentifierStack> _allItems, ItemSearch itemSearch, int page, int left , int top, int columns, int items, int xSize, int ySize, Minecraft mc, boolean displayAmount, boolean forcenumber, boolean color) {
        renderItemIdentifierStackListIntoGui(_allItems, itemSearch, page, left, top, columns, items, xSize, ySize, mc, displayAmount, forcenumber, true, false);
    }

    public static void renderItemIdentifierStackListIntoGui(List<ItemIdentifierStack> _allItems, ItemSearch itemSearch, int page, int left , int top, int columns, int items, int xSize, int ySize, Minecraft mc, boolean displayAmount, boolean forcenumber, boolean color, boolean disableEffect) {
        GL11.glPushMatrix();
        int ppi = 0;
        int column = 0;
        int row = 0;
        TextRenderer textRenderer = mc.textRenderer;
        ItemRenderer itemRenderer = new ItemRenderer();
        for(ItemIdentifierStack itemStack : _allItems) {
            if(itemStack == null) {
                column++;
                if (column >= columns){
                    row++;
                    column = 0;
                }
                ppi++;
                continue;
            }
            ItemIdentifier item = itemStack.getItem();
            if(itemSearch!= null && !itemSearch.itemSearched(item)) continue;
            ppi++;

            if (ppi <= items * page) continue;
            if (ppi > items * (page+1)) continue;
            ItemStack st = itemStack.makeNormalStack();
            int x = left + xSize * column;
            int y = top + ySize * row;

            GL11.glPushMatrix();
            GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
            Lighting.turnOn();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();

            if(st != null && itemStack.getItem() != null) {
                if(disableEffect) {
                    itemRenderer.renderGuiItem(textRenderer, mc.textureManager, st, x, y);
                } else {
                    GL11.glTranslated(0, 0, 3.0);
                    itemRenderer.renderGuiItem(textRenderer, mc.textureManager, st, x, y);
                    GL11.glTranslated(0, 0, -3.0);
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            Lighting.turnOff();

            if(displayAmount) {
                String s;
                if (st.count == 1 && !forcenumber){
                    s = "";
                } else if (st.count < 1000) {
                    s = st.count + "";
                } else if (st.count < 100000){
                    s = st.count / 1000 + "K";
                } else if (st.count < 1000000){
                    s = "0M" + st.count / 100000;
                } else {
                    s = st.count / 1000000 + "M";
                }

                GL11.glDisable(2896 /*GL_LIGHTING*/);
                GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
                textRenderer.drawWithShadow(s, x + 16 - textRenderer.getWidth(s), y + 8, 0xFFFFFF);
                GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
            }

            column++;
            if (column >= columns){
                row++;
                column = 0;
            }
        }
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glPopMatrix();
    }

    public static String getCuttedString(String input, int maxLength, TextRenderer renderer) {
        if(renderer.getWidth(input) < maxLength) {
            return input;
        }
        input += "...";
        while(renderer.getWidth(input) > maxLength && !input.isEmpty()) {
            input = input.substring(0, input.length() - 4) + "...";
        }
        return input;
    }
}
