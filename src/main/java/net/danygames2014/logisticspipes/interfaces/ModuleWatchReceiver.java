package net.danygames2014.logisticspipes.interfaces;

import net.minecraft.entity.player.PlayerEntity;

public interface ModuleWatchReceiver {
    void startWatching(PlayerEntity player) ;
    void stopWatching(PlayerEntity player);
}
