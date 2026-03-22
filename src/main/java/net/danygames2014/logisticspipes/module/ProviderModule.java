package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsOrderManager;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.CroppedInventory;
import net.danygames2014.logisticspipes.util.InventoryUtil;
import net.danygames2014.logisticspipes.util.SimpleInventory;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

public class ProviderModule implements LogisticsModule, LegacyActiveModule, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive {
    protected InventoryProvider invProvider;
    protected SendRoutedItem itemSender;

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

    public LinkedList<ItemStack> displayList = new LinkedList<>();
    public LinkedList<ItemStack> oldList = new LinkedList<>();

    private final List<PlayerEntity> localModeWatchers = new ArrayList<>();

    public ProviderModule() {}

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
        this.itemSender = itemSender;
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
    public int getGuiHandlerID() {
//        return GuiIDs.GUI_Module_Provider_ID;
        return 0;
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
        if (++currentTick < ticksToAction) return;
        currentTick = 0;
        checkUpdate(null);
        while (orderManager.hasOrders()) {
            Pair<ItemStack,RequestItems> order = orderManager.getNextRequest();
            int sent = sendItem(order.getValue1(), order.getValue1().count, order.getValue2().getRouter().getRouterId());

            if (sent > 0) {
                orderManager.sendSuccessfull(sent);
            } else {
                orderManager.sendFailed();
                break;
            }
        }
    }

    @Override
    public void canProvide(RequestTreeNode tree, Map<ItemStack, Integer> donePromisses) {
        int canProvide = getAvailableItemCount(tree.getStack());
        if (donePromisses.containsKey(tree.getStack())) {
            canProvide -= donePromisses.get(tree.getStack());
        }
        if (canProvide < 1) return;
        LogisticsPromise promise = new LogisticsPromise();
        promise.item = tree.getStack();
        promise.numberOfItems = Math.min(canProvide, tree.getMissingItemCount());
        //TODO: FIX THIS CAST
        promise.sender = (ProvideItems) itemSender;
        tree.addPromise(promise);
    }

    @Override
    public void fullFill(LogisticsPromise promise, RequestItems destination) {
        orderManager.addOrder(new ItemStack(promise.item.getItem(), promise.numberOfItems), destination);
    }

    @Override
    public int getAvailableItemCount(ItemStack item) {
        return getTotalItemCount(item) - orderManager.totalItemsCountInOrders(item);
    }

    @Override
    public HashMap<ItemStack, Integer> getAllItems() {
        HashMap<ItemStack, Integer> allItems = new HashMap<ItemStack, Integer>();
        if (invProvider.getInventory() == null) return allItems;

        InventoryUtil inv = getAdaptedUtil(invProvider.getInventory());
        HashMap<ItemStack, Integer> currentInv = inv.getItemsAndCount();
        for (ItemStack currItem : currentInv.keySet()){
            if ( hasFilter() && ((isExcludeFilter && itemIsFiltered(currItem))
                                         || (!isExcludeFilter && !itemIsFiltered(currItem)))) continue;

            if (!allItems.containsKey(currItem)){
                allItems.put(currItem, currentInv.get(currItem));
            }else {
                allItems.put(currItem, allItems.get(currItem) + currentInv.get(currItem));
            }
        }

        //Reduce what has been reserved.
        Iterator<ItemStack> iterator = allItems.keySet().iterator();
        while(iterator.hasNext()){
            ItemStack item = iterator.next();

            int remaining = allItems.get(item) - orderManager.totalItemsCountInOrders(item);
            if (remaining < 1){
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

    protected int sendItem(ItemStack item, int maxCount, Long destination) {
        int sent = 0;
        if (invProvider.getInventory() == null) return 0;
        InventoryUtil inv = getAdaptedUtil(invProvider.getInventory());
        if (inv.itemCount(item)> 0){
            ItemStack removed = inv.getSingleItem(item);
            itemSender.sendStack(removed, destination);
            sent++;
            maxCount--;
        }

        return sent;
    }

    public int getTotalItemCount(ItemStack item) {

        if (invProvider.getInventory() == null) return 0;

        if (!filterUtil.getItemsAndCount().isEmpty()
                    && ((this.isExcludeFilter && filterUtil.getItemsAndCount().containsKey(item))
                                || ((!this.isExcludeFilter) && !filterUtil.getItemsAndCount().containsKey(item)))) return 0;

        InventoryUtil inv = getAdaptedUtil(invProvider.getInventory());
        return inv.itemCount(item);
    }

    private boolean hasFilter() {
        return filterUtil.getItemsAndCount().size() > 0;
    }

    public boolean itemIsFiltered(ItemStack item){
        return filterUtil.getItemsAndCount().containsKey(item);
    }

    public InventoryUtil getAdaptedUtil(Inventory base){
        switch(extractionMode){
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

    public ExtractionMode getExtractionMode(){
        return extractionMode;
    }

    public void nextExtractionMode() {
        extractionMode = extractionMode.next();
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
        HashMap<ItemStack, Integer> list = getAllItems();
        for(ItemStack item :list.keySet()) {
            displayList.add(new ItemStack(item.getItem(), list.get(item)));
        }
        if(!oldList.equals(displayList)) {
//            MainProxy.sendToPlayerList(new PacketModuleInvContent(NetworkConstants.MODULE_INV_CONTENT, xCoord, yCoord, zCoord, slot, displayList).getPacket(), localModeWatchers);
            oldList.clear();
            oldList.addAll(displayList);
        }
        if(player != null) {
//            PacketDispatcher.sendPacketToPlayer(new PacketModuleInvContent(NetworkConstants.MODULE_INV_CONTENT, xCoord, yCoord, zCoord, slot, displayList).getPacket(), (Player)player);
        }
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
        checkUpdate(player);
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
        displayList.clear();
        displayList.addAll(list);
    }

    void markDirty(){

    }
}
