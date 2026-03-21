package net.danygames2014.logisticspipes.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class BasicGuiHelper {
    public static void drawGuiBackGround(Minecraft mc, int guiLeft, int guiTop, int right, int bottom, float zLevel, boolean resetColor) {
        drawGuiBackGround(mc, guiLeft, guiTop, right, bottom, zLevel, resetColor, true, true, true, true);
    }

    private static final String BACKGROUND = "/assets/logisticspipes/stationapi/textures/gui/gui_background.png";

    public static void drawGuiBackGround(Minecraft mc, int guiLeft, int guiTop, int right, int bottom, float zLevel, boolean resetColor, boolean displayTop, boolean displayLeft, boolean displayBottom, boolean displayRight){
        if(resetColor) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        mc.textureManager.bindTexture(mc.textureManager.getTextureId(BACKGROUND));

        if(displayTop) {
            //Top Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft + 15	, guiTop + 15	, zLevel, 0.33	, 0.33);
            var9.vertex(right - 15		, guiTop + 15	, zLevel, 0.66	, 0.33);
            var9.vertex(right - 15		, guiTop		, zLevel, 0.66	, 0);
            var9.vertex(guiLeft + 15	, guiTop		, zLevel, 0.33	, 0);
            var9.draw();
        }

        if(displayLeft) {
            //Left Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft		, bottom - 15	, zLevel, 0	, 0.66);
            var9.vertex(guiLeft + 15	, bottom - 15	, zLevel, 0.33	, 0.66);
            var9.vertex(guiLeft + 15	, guiTop + 15	, zLevel, 0.33	, 0.33);
            var9.vertex(guiLeft		, guiTop + 15	, zLevel, 0	, 0.33);
            var9.draw();
        }

        if(displayBottom) {
            //Bottom Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft + 15	, bottom		, zLevel, 0.33	, 1);
            var9.vertex(right - 15		, bottom		, zLevel, 0.66	, 1);
            var9.vertex(right - 15		, bottom - 15	, zLevel, 0.66	, 0.66);
            var9.vertex(guiLeft + 15	, bottom - 15	, zLevel, 0.33	, 0.66);
            var9.draw();
        }

        if(displayRight) {
            //Right Side
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(right - 15	, bottom - 15		, zLevel, 0.66	, 0.66);
            var9.vertex(right		, bottom - 15		, zLevel, 1	, 0.66);
            var9.vertex(right		, guiTop + 15		, zLevel, 1	, 0.33);
            var9.vertex(right - 15	, guiTop + 15		, zLevel, 0.66	, 0.33);
            var9.draw();
        }

        if(displayTop && displayLeft) {
            //Top Left
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft		, guiTop + 15	, zLevel, 0	, 0.33);
            var9.vertex(guiLeft + 15	, guiTop + 15	, zLevel, 0.33	, 0.33);
            var9.vertex(guiLeft + 15	, guiTop		, zLevel, 0.33	, 0);
            var9.vertex(guiLeft		, guiTop		, zLevel, 0	, 0);
            var9.draw();
        }

        if(displayBottom && displayLeft) {
            //Bottom Left
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(guiLeft		, bottom		, zLevel, 0	, 1);
            var9.vertex(guiLeft + 15	, bottom		, zLevel, 0.33	, 1);
            var9.vertex(guiLeft + 15	, bottom - 15	, zLevel, 0.33	, 0.66);
            var9.vertex(guiLeft		, bottom - 15	, zLevel, 0	, 0.66);
            var9.draw();
        }

        if(displayBottom && displayRight) {
            //Bottom Right
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(right - 15	, bottom			, zLevel, 0.66	, 1);
            var9.vertex(right		, bottom			, zLevel, 1	, 1);
            var9.vertex(right		, bottom - 15		, zLevel, 1	, 0.66);
            var9.vertex(right - 15	, bottom - 15		, zLevel, 0.66	, 0.66);
            var9.draw();
        }

        if(displayTop && displayRight) {
            //Top Right
            Tessellator var9 = Tessellator.INSTANCE;
            var9.startQuads();
            var9.vertex(right - 15	, guiTop + 15			, zLevel, 0.66	, 0.33);
            var9.vertex(right		, guiTop + 15			, zLevel, 1	, 0.33);
            var9.vertex(right		, guiTop				, zLevel, 1	, 0);
            var9.vertex(right - 15	, guiTop				, zLevel, 0.66	, 0);
            var9.draw();
        }

        //Center
        Tessellator var9 = Tessellator.INSTANCE;
        var9.startQuads();
        var9.vertex(guiLeft + 15	, bottom - 15		, zLevel, 0.33	, 0.66);
        var9.vertex(right - 15		, bottom - 15		, zLevel, 0.66	, 0.66);
        var9.vertex(right - 15		, guiTop + 15		, zLevel, 0.66	, 0.33);
        var9.vertex(guiLeft + 15	, guiTop + 15		, zLevel, 0.33	, 0.33);
        var9.draw();
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(double par1, double par2, double par3, double par4, double par5, double par6, float zLevel)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.INSTANCE;
        var9.startQuads();
        var9.vertex(par1 + 0, par2 + par6, zLevel, (float)(par3 + 0) * var7, (float)(par4 + par6) * var8);
        var9.vertex(par1 + par5, par2 + par6, zLevel, (float)(par3 + par5) * var7, (float)(par4 + par6) * var8);
        var9.vertex(par1 + par5, par2 + 0, zLevel, (float)(par3 + par5) * var7, (float)(par4 + 0) * var8);
        var9.vertex(par1 + 0, par2 + 0, zLevel, (float)(par3 + 0) * var7, (float)(par4 + 0) * var8);
        var9.draw();
    }
}
