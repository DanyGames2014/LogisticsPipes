package net.danygames2014.logisticspipes.mixin;

import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    void renderTag(ItemEntity item, double x, double y, double z, float rotation, float delta, CallbackInfo ci){
        if (item instanceof RoutedItemEntity routedItem && Minecraft.isDisplayGui() && Minecraft.INSTANCE.options.debugHud) {
            float var10 = routedItem.getDistance(Minecraft.INSTANCE.camera);
            if (!(var10 > (float)10)) {
                String name = String.valueOf(routedItem.destinationUUID);
                TextRenderer var11 = Minecraft.INSTANCE.textRenderer;
                float var12 = 1.6F;
                float var13 = 0.016666668F * var12;
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.0F, (float) y + 0.9F, (float) z);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-Minecraft.INSTANCE.camera.yaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(Minecraft.INSTANCE.camera.pitch, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-var13, -var13, var13);
                GL11.glDisable(2896);
                GL11.glDepthMask(false);
                GL11.glDisable(2929);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                Tessellator var14 = Tessellator.INSTANCE;
                byte var15 = 0;
                GL11.glDisable(3553);
                var14.startQuads();
                int var16 = var11.getWidth(name) / 2;
                var14.color(0.0F, 0.0F, 0.0F, 0.25F);
                var14.vertex(-var16 - 1, -1 + var15, 0.0F);
                var14.vertex(-var16 - 1, 8 + var15, 0.0F);
                var14.vertex(var16 + 1, 8 + var15, 0.0F);
                var14.vertex(var16 + 1, -1 + var15, 0.0F);
                var14.draw();
                GL11.glEnable(3553);
                var11.draw(name, -var11.getWidth(name) / 2, var15, 553648127);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                var11.draw(name, -var11.getWidth(name) / 2, var15, -1);
                GL11.glEnable(2896);
                GL11.glDisable(3042);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }
    }
}
