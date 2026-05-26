package net.danygames2014.logisticspipes.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

public class InvisibleBlockEntityRenderer extends BlockEntityRenderer {
    public static InvisibleBlockEntityRenderer INSTANCE = new InvisibleBlockEntityRenderer();

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {

    }
}
