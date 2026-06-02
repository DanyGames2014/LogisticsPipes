package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.gui.hud.modules.ItemSinkHud;
import net.danygames2014.logisticspipes.gui.hud.modules.PassiveSupplierHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.network.UpdateModuleInventoryContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerModuleWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.PassiveSupplierScreenHandler;
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

public class PassiveSupplierModule implements LogisticsModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive, Inventory {
    private final SimpleInventory filterInventory = new SimpleInventory(9, "Requested items", 64, this::markDirty);
    private InventoryProvider invProvider;
    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();

    private PassiveSupplierHud HUD = new PassiveSupplierHud(this);

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
    }

    public Inventory getFilterInventory() {
        return filterInventory;
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        Inventory targetInventory = invProvider.getInventory();
        if (targetInventory == null) return null;

        InventoryUtil filterUtil = new InventoryUtil(filterInventory, false);
        if (!filterUtil.containsItem(ItemIdentifier.get(item))) return null;

        int targetCount = filterUtil.getItemCount(ItemIdentifier.get(item));
        InventoryUtil targetUtil = new InventoryUtil(filterInventory, false);
        if (targetCount <= targetUtil.getItemCount(ItemIdentifier.get(item))) return null;

        SinkReply reply = new SinkReply();
        reply.fixedPriority = SinkReply.FixedPriority.PassiveSupplier;
        reply.isPassive = true;
        return reply;
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
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {
    }

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        list.add("Supplied: ");
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
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("passive_supplier");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new PassiveSupplierScreenHandler(player, this);
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
        PacketHelper.sendTo(player, new UpdateModuleInventoryContentS2CPacket(x, y, z, slot, ItemIdentifierStack.getListFromInventory(filterInventory)));
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
}
