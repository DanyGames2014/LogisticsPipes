package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.interfaces.InventoryProvider;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.SendRoutedItem;
import net.danygames2014.logisticspipes.interfaces.WorldProvider;
import net.danygames2014.logisticspipes.util.InventoryUtil;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;

public class PolymorphicItemSinkModule implements LogisticsModule {
    private InventoryProvider invProvider;

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
    }

    @Override
    public void registerPosition(int x, int y, int z, int slot) {

    }

    @Override
    public Identifier getScreenIdentifier() {
        return null;
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        Inventory targetInventory = invProvider.getInventory();
        if (targetInventory == null) {
            return null;
        }

        InventoryUtil invUtil = new InventoryUtil(targetInventory, false);
        if (!invUtil.containsItem(ItemIdentifier.get(item))) {
            return null;
        }

        SinkReply reply = new SinkReply();
        reply.fixedPriority = SinkReply.FixedPriority.ItemSink;
        reply.isDefault = false;
        reply.isPassive = true;
        return reply;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {

    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {

    }
}
