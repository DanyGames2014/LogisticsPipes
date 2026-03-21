package net.danygames2014.logisticspipes.item;

import net.danygames2014.logisticspipes.client.particle.SparkleParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class MagicWand extends TemplateItem {
    public MagicWand(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getTextureId(int damage) {
        return Item.STICK.getTextureId(damage);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockPos offset = blockPos.offset(Direction.byId(side));

        float boundry = 0.4F;
        int pipeWidth = 3;

        float width = boundry + world.random.nextInt(pipeWidth) / 10.0F;
        float length = boundry + world.random.nextInt(pipeWidth) / 10.0F;
        float height = world.random.nextInt(7) / 10.0F + 0.2F;

        float scalemult = 1f + (float)Math.log10(4);

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            Minecraft.INSTANCE.particleManager.addParticle(new SparkleParticle(world, offset.getX() + length, offset.getY() + height, offset.getZ() + width, scalemult, 1f, 1f, 0f, 6 + world.random.nextInt(3)));
        }

        return true;
    }
}
