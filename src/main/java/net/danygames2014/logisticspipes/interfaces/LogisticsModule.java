package net.danygames2014.logisticspipes.interfaces;

import net.danygames2014.logisticspipes.interfaces.routing.SaveState;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.util.Identifier;

public interface LogisticsModule extends SaveState {
    /**
     * Registers the Inventory and ItemSender to the module
     *
     * @param invProvider The connected inventory
     * @param itemSender  the handler to send items into the logistics system
     * @param world       that the module is in.
     */
    void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world);

    /**
     * Registers the position to the module
     */
    void registerPosition(int x, int y, int z, int slot);

    /**
     *
     * @return The gui identifier of the given module;
     */
    Identifier getScreenIdentifier();

    default ScreenHandler getScreenHandler(PlayerEntity player) {
        return new ModuleScreenHandler(player, null);
    }

    /**
     * Gives an sink answer on the given itemstack
     *
     * @param item to sink
     * @return SinkReply wether the module sinks the item or not
     */
    SinkReply sinksItem(ItemStack item);

    /**
     * Returns submodules. Normal modules don't have submodules
     *
     * @param slot of the requested module
     */
    LogisticsModule getSubModule(int slot);

    /**
     * A tick for the Module
     */
    void tick();
}
