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

public class ItemSinkModule implements LogisticsModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive {
    private final SimpleInventory filterInventory = new SimpleInventory(9, "Requested items", 1, this::markDirty);
    private boolean isDefaultRoute;
    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

//    private IHUDModuleRenderer HUD = new HUDItemSink(this);

    private final List<PlayerEntity> localModeWatchers = new ArrayList<>();

    public Inventory getFilterInventory(){
        return filterInventory;
    }

    public boolean isDefaultRoute(){
        return isDefaultRoute;
    }

    public void setDefaultRoute(boolean isDefaultRoute){
        this.isDefaultRoute = isDefaultRoute;
//        MainProxy.sendToPlayerList(new PacketModuleInteger(NetworkConstants.ITEM_SINK_STATUS, xCoord, yCoord, zCoord, slot, isDefaultRoute() ? 1 : 0).getPacket(), localModeWatchers);
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
        if (invUtil.containsItem(item)){
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.ItemSink;
            reply.isPassive = true;
            return reply;
        }
        if (isDefaultRoute){
            SinkReply reply = new SinkReply();
            reply.fixedPriority = SinkReply.FixedPriority.DefaultRoute;
            reply.isPassive = true;
            reply.isDefault = true;
            return reply;
        }
        return null;
    }

    @Override
    public int getGuiHandlerID() {
        return 0;
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
    public void tick() {}

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<String>();
        list.add("Default: " + (isDefaultRoute() ? "Yes" : "No"));
        list.add("Filter: ");
        list.add("<inventory>");
        list.add("<that>");
        return list;
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
//        PacketDispatcher.sendPacketToPlayer(new PacketModuleInteger(NetworkConstants.ITEM_SINK_STATUS, xCoord, yCoord, zCoord, slot, isDefaultRoute() ? 1 : 0).getPacket(), (Player)player);
    }

    @Override
    public void stopWatching(PlayerEntity player) {
        localModeWatchers.remove(player);
    }

    public void markDirty() {
//        MainProxy.sendToPlayerList(new PacketModuleInvContent(NetworkConstants.MODULE_INV_CONTENT, xCoord, yCoord, zCoord, slot, ItemIdentifierStack.getListFromInventory(inventory)).getPacket(), localModeWatchers);
    }

    @Override
    public HUDModuleRenderer getRenderer() {
        return null;
    }

    @Override
    public void handleInvContent(LinkedList<ItemStack> list) {
        filterInventory.handleItemStackList(list);
    }
}
