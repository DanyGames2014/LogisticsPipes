package net.danygames2014.logisticspipes.gui;

import net.danygames2014.logisticspipes.client.gui.screen.LogisticsBaseScreen;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public interface ScreenWithDisk {
    ItemStack getDisk();

    LogisticsBaseScreen getScreen();

    LinkedList<ItemIdentifierStack> getAllItems();

    int getX();
    int getY();
    int getZ();

    PlayerEntity getPlayer();
}
