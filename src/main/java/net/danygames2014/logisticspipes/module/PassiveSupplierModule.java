package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.util.InventoryUtil;
import net.danygames2014.logisticspipes.util.SimpleInventory;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PassiveSupplierModule implements LogisticsModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive {
    private final SimpleInventory filterInventory = new SimpleInventory(9, "Requested items",64, this::markDirty);
    private InventoryProvider invProvider;
    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private final List<PlayerEntity> localModeWatchers = new ArrayList<>();

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
    }

    public Inventory getFilterInventory(){
        return filterInventory;
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        Inventory targetInventory = invProvider.getInventory();
        if (targetInventory == null) return null;

        InventoryUtil filterUtil = new InventoryUtil(filterInventory, false);
        if (!filterUtil.containsItem(item)) return null;

        int targetCount = filterUtil.getItemCount(item);
        InventoryUtil targetUtil = new InventoryUtil(filterInventory, false);
        if (targetCount <= targetUtil.getItemCount(item)) return null;

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
    public void tick() {}

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
    public int getGuiHandlerID() {
        return 0;
    }

    @Override
    public void startWatching() {
//        PacketDispatcher.sendPacketToServer(new PacketPipeInteger(NetworkConstants.HUD_START_WATCHING_MODULE, xCoord, yCoord, zCoord, slot).getPacket());
    }

    @Override
    public void stopWatching() {
//        PacketDispatcher.sendPacketToServer(new PacketPipeInteger(NetworkConstants.HUD_START_WATCHING_MODULE, xCoord, yCoord, zCoord, slot).getPacket());
    }

    @Override
    public void startWatching(PlayerEntity player) {
        localModeWatchers.add(player);
//        PacketDispatcher.sendPacketToPlayer(new PacketModuleInvContent(NetworkConstants.MODULE_INV_CONTENT, xCoord, yCoord, zCoord, slot, ItemIdentifierStack.getListFromInventory(_filterInventory)).getPacket(), (Player)player);
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
    public void handleInvContent(LinkedList<ItemStack> list) {
        filterInventory.handleItemStackList(list);
    }

    void markDirty(){

    }
}
