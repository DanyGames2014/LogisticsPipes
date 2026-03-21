package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.client.render.LogisticsHUDRenderer;
import net.minecraft.world.World;

public interface HUDRendererProvider {
    HUDRenderer getRenderer();
    int getX();
    int getY();
    int getZ();
    World getWorld();
    void startWatching();
    void stopWatching();
}
