package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.gui.hud.modules.ItemSinkHud;
import net.danygames2014.logisticspipes.gui.hud.modules.ProviderModuleHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.network.UpdateModuleInventoryContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerModuleWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsOrderManager;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.screen.handler.ProviderScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.*;

public class ProviderModule implements LogisticsModule, LegacyActiveModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive, Inventory {
    protected InventoryProvider invProvider;
    protected SendRoutedItem itemSender;
    protected WorldProvider worldProvider;

    protected LogisticsOrderManager orderManager = new LogisticsOrderManager();

    private final SimpleInventory filterInventory = new SimpleInventory(9, "Items to provide (or empty for all)", 1, this::markDirty);
    private final InventoryUtil filterUtil = new InventoryUtil(filterInventory, false);

    protected final int ticksToAction = 6;
    protected int currentTick = 0;

    protected boolean isExcludeFilter = false;
    protected ExtractionMode extractionMode = ExtractionMode.Normal;

    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    public LinkedList<ItemIdentifierStack> displayList = new LinkedList<>();
    public LinkedList<ItemIdentifierStack> oldList = new LinkedList<>();

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();

    private ProviderModuleHud HUD = new ProviderModuleHud(this);

    public ProviderModule() {
    }

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
        this.itemSender = itemSender;
        this.worldProvider = world;
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {
        filterInventory.readNbt(nbt, "");
        isExcludeFilter = nbt.getBoolean("filterisexclude");
        extractionMode = ExtractionMode.values()[nbt.getInt("extractionMode")];
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        filterInventory.writeNbt(nbt, "");
        nbt.putBoolean("filterisexclude", isExcludeFilter);
        nbt.putInt("extractionMode", extractionMode.ordinal());
    }

    @Override
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("provider");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new ProviderScreenHandler(player, this);
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        return null;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {
        if(worldProvider.getWorld().isRemote) return;
        if (++currentTick < ticksToAction) return;
        currentTick = 0;
        checkUpdate(null);
        while (orderManager.hasOrders()) {
            Pair<ItemIdentifierStack, RequestItems> order = orderManager.getNextRequest();
            int sent = sendItem(order.getValue1().getItem(), order.getValue1().stackSize, order.getValue2().getRouter().getRouterId());

            if (sent > 0) {
                orderManager.sendSuccessfull(sent);
                if(worldProvider.getWorld().getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
                    pipe.queueParticle(ParticleColor.VIOLET, 3);
                }
            } else {
                orderManager.sendFailed();
                break;
            }
        }
    }

    @Override
    public void canProvide(RequestTreeNode tree, Map<ItemIdentifier, Integer> donePromisses) {
        int canProvide = getAvailableItemCount(tree.getStack().getItem());
        if (donePromisses.containsKey(tree.getStack().getItem())) {
            canProvide -= donePromisses.get(tree.getStack().getItem());
        }
        if (canProvide < 1) return;
        LogisticsPromise promise = new LogisticsPromise();
        promise.item = tree.getStack().getItem();
        promise.numberOfItems = Math.min(canProvide, tree.getMissingItemCount());
        //TODO: FIX THIS CAST
        promise.sender = (ProvideItems) itemSender;
        tree.addPromise(promise);
    }

    @Override
    public void fullFill(LogisticsPromise promise, RequestItems destination) {
        orderManager.addOrder(new ItemIdentifierStack(promise.item, promise.numberOfItems), destination);
    }

    @Override
    public int getAvailableItemCount(ItemIdentifier item) {
        return getTotalItemCount(item) - orderManager.totalItemsCountInOrders(item);
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getAllItems() {
        HashMap<ItemIdentifier, Integer> allItems = new HashMap<>();
        if (invProvider.getInventory() == null) return allItems;

        InventoryUtil inv = getAdaptedUtil(invProvider.getInventory());
        HashMap<ItemIdentifier, Integer> currentInv = inv.getItemsAndCount();
        for (ItemIdentifier currItem : currentInv.keySet()) {
            if (hasFilter() && ((isExcludeFilter && itemIsFiltered(currItem))
                    || (!isExcludeFilter && !itemIsFiltered(currItem)))) continue;

            if (!allItems.containsKey(currItem)) {
                allItems.put(currItem, currentInv.get(currItem));
            } else {
                allItems.put(currItem, allItems.get(currItem) + currentInv.get(currItem));
            }
        }

        //Reduce what has been reserved.
        Iterator<ItemIdentifier> iterator = allItems.keySet().iterator();
        while (iterator.hasNext()) {
            ItemIdentifier item = iterator.next();

            int remaining = allItems.get(item) - orderManager.totalItemsCountInOrders(item);
            if (remaining < 1) {
                iterator.remove();
            } else {
                allItems.put(item, remaining);
            }
        }

        return allItems;
    }

    @Override
    public Router getRouter() {
        //THIS IS NEVER SUPPOSED TO HAPPEN
        return null;
    }

    protected int sendItem(ItemIdentifier item, int maxCount, long destination) {
        int sent = 0;
        if (invProvider.getInventory() == null) return 0;
        InventoryUtil inv = getAdaptedUtil(invProvider.getInventory());
        if (inv.itemCount(item) > 0) {
            ItemStack removed = inv.getSingleItem(item);
            itemSender.sendStack(removed, destination);
            sent++;
            maxCount--;
        }

        return sent;
    }

    public int getTotalItemCount(ItemIdentifier item) {

        if (invProvider.getInventory() == null) return 0;

        if (!filterUtil.getItemsAndCount().isEmpty()
                && ((this.isExcludeFilter && filterUtil.getItemsAndCount().containsKey(item))
                || ((!this.isExcludeFilter) && !filterUtil.getItemsAndCount().containsKey(item)))) return 0;

        InventoryUtil inv = getAdaptedUtil(invProvider.getInventory());
        return inv.itemCount(item);
    }

    private boolean hasFilter() {
        return !filterUtil.getItemsAndCount().isEmpty();
    }

    public boolean itemIsFiltered(ItemIdentifier item) {
        return filterUtil.getItemsAndCount().containsKey(item);
    }

    public InventoryUtil getAdaptedUtil(Inventory base) {
        switch (extractionMode) {
            case LeaveFirst:
                base = new CroppedInventory(base, 1, 0);
                break;
            case LeaveLast:
                base = new CroppedInventory(base, 0, 1);
                break;
            case LeaveFirstAndLast:
                base = new CroppedInventory(base, 1, 1);
                break;
            case Leave1PerStack:
                return new InventoryUtil(base, true);
        }
        return new InventoryUtil(base, false);
    }

    /*** GUI STUFF ***/
    public Inventory getFilterInventory() {
        return filterInventory;
    }

    public boolean isExcludeFilter() {
        return isExcludeFilter;
    }

    public void setFilterExcluded(boolean isExcludeFilter) {
        this.isExcludeFilter = isExcludeFilter;
    }

    public ExtractionMode getExtractionMode() {
        return extractionMode;
    }

    public void nextExtractionMode() {
        extractionMode = extractionMode.next();
    }

    public void setExtractionMode(ExtractionMode extractionMode) {
        this.extractionMode = extractionMode;
    }

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        list.add(!isExcludeFilter ? "Included" : "Excluded");
        list.add("Mode: " + extractionMode.getExtractionModeString());
        list.add("Filter: ");
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

    private void checkUpdate(PlayerEntity player) {
        displayList.clear();
        HashMap<ItemIdentifier, Integer> list = getAllItems();
        for (ItemIdentifier item : list.keySet()) {
            displayList.add(new ItemIdentifierStack(item, list.get(item)));
        }
        if (!oldList.equals(displayList)) {
            PacketUtil.sendToPlayerList(new UpdateModuleInventoryContentS2CPacket(x, y, z, slot, displayList), localModeWatchers);
            oldList.clear();
            oldList.addAll(displayList);
        }
        if (player != null) {
            PacketHelper.sendTo(player, new UpdateModuleInventoryContentS2CPacket(x, y, z, slot, displayList));
        }
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
        checkUpdate(player);
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
        displayList.clear();
        displayList.addAll(list);
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

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return filterInventory.canPlayerUse(player);
    }
}
