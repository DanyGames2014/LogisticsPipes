package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.entity.player.PlayerEntity;

public interface WatchingHandler {
    void playerStartWatching(PlayerEntity player, int mode);
    void playerStopWatching(PlayerEntity player, int mode);
}
