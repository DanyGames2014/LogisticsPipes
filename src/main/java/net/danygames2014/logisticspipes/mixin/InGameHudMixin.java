package net.danygames2014.logisticspipes.mixin;

import net.danygames2014.logisticspipes.client.render.LogisticsHUDRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private long renderTicks=0;
    @Inject(method = "render", at = @At("HEAD"))
    void renderLPHud(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci){
        if(LogisticsHUDRenderer.getInstance().displayRenderer()) {
            GL11.glPushMatrix();
            //Orientation
            Minecraft.INSTANCE.gameRenderer.applyCameraTransform(tickDelta);
            LogisticsHUDRenderer.getInstance().renderWorldRelative(renderTicks, tickDelta);
//            mc.entityRenderer.setupOverlayRendering();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
//            LogisticsHUDRenderer.getInstance().renderPlayerDisplay(renderTicks);
            GL11.glPopMatrix();
        }
    }
}
