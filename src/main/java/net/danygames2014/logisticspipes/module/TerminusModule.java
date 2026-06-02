package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.network.UpdatePlayerModuleWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.TerminusScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TerminusModule implements LogisticsModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive, Inventory {
    private final SimpleInventory filterInventory = new SimpleInventory(9, "Terminated items", 1, this::markDirty);

    private int x;
    private int y;
    private int z;
    private int slot;

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();

    public TerminusModule() {
    }

    public SimpleInventory getFilterInventory() {
        return filterInventory;
    }

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {

    }

    @Override
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("terminus");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new TerminusScreenHandler(player, this);
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {
        filterInventory.readNbt(nbt, "");
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        filterInventory.writeNbt(nbt, "");
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        InventoryUtil invUtil = new InventoryUtil(filterInventory, false);
        if (invUtil.containsItem(ItemIdentifier.get(item))) {
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.Terminus;
            reply.isPassive = true;
            return reply;
        }
        return null;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        list.add("Terminated: ");
        list.add("<inventory>");
        list.add("<that>");
        return list;
    }

    @Override
    public void registerPosition(int x, int y, int z, int slot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slot = slot;
    }

    @Override
    public void startWatching() {
        PacketHelper.send(new UpdatePlayerModuleWatchingStatusC2SPacket(x, y, z, slot, true));
    }

    @Override
    public void stopWatching() {
        PacketHelper.send(new UpdatePlayerModuleWatchingStatusC2SPacket(x, y, z, slot, false));
    }

    @Override
    public void startWatching(PlayerEntity player) {
        localModeWatchers.add(player);
    }

    @Override
    public void stopWatching(PlayerEntity player) {
        localModeWatchers.remove(player);
    }

    @Override
    public HUDModuleRenderer getRenderer() {
        return null;
    }

    @Override
    public void handleInvContent(LinkedList<ItemIdentifierStack> list) {
        filterInventory.handleItemStackList(list);
    }

    // Inventory
    @Override
    public int size() {
        return filterInventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return filterInventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return filterInventory.removeStack(slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        filterInventory.setStack(slot, stack);
    }

    @Override
    public String getName() {
        return filterInventory.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return filterInventory.getMaxCountPerStack();
    }

    public void markDirty() {
//        MainProxy.sendToPlayerList(new PacketModuleInvContent(NetworkConstants.MODULE_INV_CONTENT, xCoord, yCoord, zCoord, slot, ItemIdentifierStack.getListFromInventory(inventory)).getPacket(), localModeWatchers);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return filterInventory.canPlayerUse(player);
    }
}
