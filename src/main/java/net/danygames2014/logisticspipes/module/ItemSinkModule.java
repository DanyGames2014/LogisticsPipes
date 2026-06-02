package net.danygames2014.logisticspipes.module;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.gui.hud.modules.ItemSinkHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.network.UpdateModuleDataS2CPacket;
import net.danygames2014.logisticspipes.network.UpdateModuleInventoryContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerModuleWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.ItemSinkScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ItemSinkModule implements LogisticsModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive, Inventory, Serializable {
    protected final SimpleInventory filterInventory = new SimpleInventory(9, "Requested items", 1, this::markDirty);
    protected boolean isDefaultRoute;
    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private ItemSinkHud HUD = new ItemSinkHud(this);

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();

    public Inventory getFilterInventory() {
        return filterInventory;
    }

    public boolean isDefaultRoute() {
        return isDefaultRoute;
    }

    public void setDefaultRoute(boolean isDefaultRoute) {
        this.isDefaultRoute = isDefaultRoute;
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            PacketUtil.sendToPlayerList(new UpdateModuleDataS2CPacket(x, y, z, slot, this), localModeWatchers);
        }
    }

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {

    }

    @Override
    public void registerPosition(int x, int y, int z, int slot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slot = slot;
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        InventoryUtil invUtil = new InventoryUtil(filterInventory, false);
        if (invUtil.containsItem(ItemIdentifier.get(item))) {
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.ItemSink;
            reply.isPassive = true;
            return reply;
        }
        if (isDefaultRoute) {
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.DefaultRoute;
            reply.isPassive = true;
            reply.isDefault = true;
            return reply;
        }
        return null;
    }

    @Override
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("item_sink");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new ItemSinkScreenHandler(player, this);
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {
        filterInventory.readNbt(nbt, "");
        setDefaultRoute(nbt.getBoolean("defaultdestination"));
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        filterInventory.writeNbt(nbt, "");
        nbt.putBoolean("defaultdestination", isDefaultRoute());
    }

    @Override
    public void tick() {
    }

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        list.add("Default: " + (isDefaultRoute() ? "Yes" : "No"));
        list.add("Filter: ");
        list.add("<inventory>");
        list.add("<that>");
        return list;
    }

    @Override
    public void startWatching() {
        PacketHelper.send(new UpdatePlayerModuleWatchingStatusC2SPacket(x, y, z, slot, true));
    }

    @Override
    public void stopWatching() {
        PacketHelper.send(new UpdatePlayerModuleWatchingStatusC2SPacket(x, y, z, slot, true));
    }

    @Override
    public void startWatching(PlayerEntity player) {
        localModeWatchers.add(player);
        PacketHelper.sendTo(player, new UpdateModuleInventoryContentS2CPacket(x, y, z, slot, ItemIdentifierStack.getListFromInventory(filterInventory)));
        PacketHelper.sendTo(player, new UpdateModuleDataS2CPacket(x, y, z, slot, this));
    }

    @Override
    public void stopWatching(PlayerEntity player) {
        localModeWatchers.remove(player);
    }

    @Override
    public HUDModuleRenderer getRenderer() {
        return HUD;
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
        PacketUtil.sendToPlayerList(new UpdateModuleInventoryContentS2CPacket(x, y, z, slot, ItemIdentifierStack.getListFromInventory(filterInventory)), localModeWatchers);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return filterInventory.canPlayerUse(player);
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeBoolean(isDefaultRoute());
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        setDefaultRoute(stream.readBoolean());
    }
}
