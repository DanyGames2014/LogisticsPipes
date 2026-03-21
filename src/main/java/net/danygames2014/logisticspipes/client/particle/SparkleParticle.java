package net.danygames2014.logisticspipes.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class SparkleParticle extends Particle {
    public static final String TEXTURE = "/assets/logisticspipes/stationapi/textures/particles.png";

    public SparkleParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void render(Tessellator tessellator, float partialTicks, float horizontalSize, float verticalSize, float depthSize, float widthOffset, float heightOffset) {
        super.render(tessellator, partialTicks, horizontalSize, verticalSize, depthSize, widthOffset, heightOffset);
        tessellator.draw();
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, 1);
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(TEXTURE));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
        int var8 = 0 + this.particleAge / 5;
        float var9 = var8 % 8 / 8.0F;
        float var10 = var9 + 0.124875F;
        float var11 = var8 / 8 / 8.0F;
        float var12 = var11 + 0.124875F;
        float var13 = 0.1F * this.scale * ((float)(this.maxParticleAge - this.particleAge + 1) / (float)this.maxParticleAge);
        float var14 = (float)(this.prevX + (this.x - this.prevX) * partialTicks - xOffset);
        float var15 = (float)(this.prevY + (this.y - this.prevY) * partialTicks - yOffset);
        float var16 = (float)(this.prevZ + (this.z - this.prevZ) * partialTicks - zOffset);
        float var17 = 1.0F;
        tessellator.startQuads();
//        tessellator.setBrightness(240);
        tessellator.color(this.red * var17, this.green * var17, this.blue * var17, 1.0F);
        tessellator.vertex(var14 - horizontalSize * var13 - widthOffset * var13, var15 - verticalSize * var13, var16 - depthSize * var13 - heightOffset * var13, var10, var12);
        tessellator.vertex(var14 - horizontalSize * var13 + widthOffset * var13, var15 + verticalSize * var13, var16 - depthSize * var13 + heightOffset * var13, var10, var11);
        tessellator.vertex(var14 + horizontalSize * var13 + widthOffset * var13, var15 + verticalSize * var13, var16 + depthSize * var13 + heightOffset * var13, var9, var11);
        tessellator.vertex(var14 + horizontalSize * var13 - widthOffset * var13, var15 - verticalSize * var13, var16 + depthSize * var13 - heightOffset * var13, var9, var12);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId("/particles.png"));
        tessellator.startQuads();
    }

    @Override
    public void tick() {
        PlayerEntity player = Minecraft.INSTANCE.player;
        if(player != null){
            if(player.getDistance(this.x, this.y, this.z) > 50){
                this.markDead();
            }

            this.prevX = this.x;
            this.prevY = this.y;
            this.prevZ = this.z;

            if (this.particleAge++ >= this.maxParticleAge)
            {
                this.markDead();
            }

            this.velocityX -=  0.05D * this.gravityStrength - 0.1D * this.gravityStrength * new Random().nextDouble();
            this.velocityY -=  0.05D * this.gravityStrength - 0.1D * this.gravityStrength * new Random().nextDouble();
            this.velocityZ -=  0.05D * this.gravityStrength - 0.1D * this.gravityStrength * new Random().nextDouble();

            this.move(this.velocityX, this.velocityY, this.velocityZ);

            if (this.onGround)
            {
                this.velocityX *= 0.699999988079071D;
                this.velocityZ *= 0.699999988079071D;
            }
        }
    }
}
