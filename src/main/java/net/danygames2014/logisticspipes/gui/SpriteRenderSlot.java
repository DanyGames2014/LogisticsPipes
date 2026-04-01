package net.danygames2014.logisticspipes.gui;

import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;

public abstract class SpriteRenderSlot implements RenderSlot {
    public abstract Atlas.Sprite getSprite();

    public abstract boolean drawSlotSprite();

    public abstract boolean customRender(Minecraft minecraft, float zLevel);

    @Override
    public int getSize() {
        return 18;
    }
}
