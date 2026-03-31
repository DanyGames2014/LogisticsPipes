package net.danygames2014.logisticspipes.gui;

import net.minecraft.entity.player.PlayerEntity;

public interface OpenScreenController {
    void screenOpenedByPlayer(PlayerEntity player);
    void screenClosedByPlayer(PlayerEntity player);
}
