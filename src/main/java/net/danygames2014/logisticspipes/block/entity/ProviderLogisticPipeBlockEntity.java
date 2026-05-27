package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsOrderManager;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class ProviderLogisticPipeBlockEntity extends LogisticPipeBlockEntity implements ProvideItems, HUDRendererProvider, ChestContentReceiver, OrderManagerContentReceiver {
    public final List<PlayerEntity> localModeWatchers = new ArrayList<>();
    public final LinkedList<ItemIdentifierStack> itemList = new LinkedList<>();
    public final LinkedList<ItemIdentifierStack> oldList = new LinkedList<>();
    public final LinkedList<ItemIdentifierStack> itemListOrderer = new LinkedList<>();
//    private final HUDProvider HUD = new HUDProvider(this);

    protected LogisticsOrderManager orderManager = new LogisticsOrderManager(this::markInventoryDirty);

    // Logic
    private final SimpleInventory filterInventory = new SimpleInventory(9, "Items to provide (or empty for all)", 1, this::markInventoryDirty);
    private boolean filterIsExclude;
    private ExtractionMode extractionMode = ExtractionMode.Normal;

    private final InventoryUtil dummyInvUtil = new InventoryUtil(filterInventory, false);

    public ProviderLogisticPipeBlockEntity() {
        super();
    }

    public ProviderLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void openModuleScreen(PlayerEntity player) {
        GuiHelper.openGUI(player, LogisticsPipes.NAMESPACE.id("provider_pipe"), getFilterInventory(), new ModuleScreenHandler(player, getFilterInventory()), (messagePacket) -> {
            messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
        });
    }

    public int getTotalItemCount(ItemIdentifier item) {
        if (!isEnabled()){
            return 0;
        }

        //Check if configurations allow for this item
        if (hasFilter()
                    && ((isExcludeFilter() && itemIsFiltered(item))
                                || (!isExcludeFilter() && !itemIsFiltered(item)))) return 0;


        int count = 0;
        for (Direction d : Direction.values()){
            BlockEntity blockEntity = world.getBlockEntity(x + d.getOffsetX(), y + d.getOffsetY(), z + d.getOffsetZ());
            if (blockEntity instanceof PipeBlockEntity){
                continue;
            }
            if(!NyalibInventoryUtil.hasItemHandler(blockEntity)){
                continue;
            }
            InventoryUtil inv = this.getAdaptedInventoryUtil(NyalibInventoryUtil.getWrappedItemHandler(blockEntity, d));
            count += inv.itemCount(item);
        }
        return count;
    }

    protected int sendItem(ItemIdentifier item, int maxCount, long destination) {
        int sent = 0;
        for (Direction d : Direction.values()){
            BlockEntity blockEntity = world.getBlockEntity(x + d.getOffsetX(), y + d.getOffsetY(), z + d.getOffsetZ());
            if (blockEntity instanceof PipeBlockEntity){
                continue;
            }
            if(!NyalibInventoryUtil.hasItemHandler(blockEntity)){
                continue;
            }

            InventoryUtil inv = getAdaptedInventoryUtil(NyalibInventoryUtil.getWrappedItemHandler(blockEntity, d));

            if (inv.itemCount(item)> 0){
                ItemStack removed = inv.getSingleItem(item);
                RoutedItem routedItem = ItemUtil.createRoutedItem(removed, this.world);
                routedItem.setSource(this.getRouter().getRouterId());
                routedItem.setDestination(destination);
                routedItem.setTransportMode(RoutedItem.TransportMode.Active);
                super.queueRoutedItem(routedItem, d);
                sent++;
                maxCount--;
                if (maxCount < 1) break;
            }
        }
        updateInv(false);
        return sent;
    }

    private InventoryUtil getAdaptedInventoryUtil(Inventory base){
        ExtractionMode mode = getExtractionMode();
        switch(mode){
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

    @Override
    public int getAvailableItemCount(ItemIdentifier item) {
        if (!isEnabled()){
            return 0;
        }
        return getTotalItemCount(item) - orderManager.totalItemsCountInOrders(item);
    }

    @Override
    public void tick() {
        super.tick();
        if (!orderManager.hasOrders() || world.getTime() % 6 != 0) return;

        if(!this.getClass().equals(ProviderLogisticPipeBlockEntity.class)) return;

        Pair<ItemIdentifierStack,RequestItems> order = orderManager.getNextRequest();
        int sent = sendItem(order.getValue1().getItem(), order.getValue1().stackSize, order.getValue2().getRouter().getRouterId());
        if (sent > 0){
            orderManager.sendSuccessfull(sent);
        }
        else {
            orderManager.sendFailed();
        }
    }

    @Override
    public void canProvide(RequestTreeNode tree, Map<ItemIdentifier, Integer> donePromisses) {
        if (!isEnabled()){
            return;
        }

        // Check the transaction and see if we have helped already
        int canProvide = getAvailableItemCount(tree.getStack().getItem());
        if (donePromisses.containsKey(tree.getStack().getItem())){
            canProvide -= donePromisses.get(tree.getStack().getItem());
        }
        if (canProvide < 1) return;
        LogisticsPromise promise = new LogisticsPromise();
        promise.item = tree.getStack().getItem();
        promise.numberOfItems = Math.min(canProvide, tree.getMissingItemCount());
        promise.sender = this;
        tree.addPromise(promise);
    }

    @Override
    public void fullFill(LogisticsPromise promise, RequestItems destination) {
        orderManager.addOrder(new ItemIdentifierStack(promise.item, promise.numberOfItems), destination);
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getAllItems() {
        HashMap<ItemIdentifier, Integer> allItems = new HashMap<>();

        if (!isEnabled()){
            return allItems;
        }

        for (Direction d : Direction.values()){
            BlockEntity blockEntity = world.getBlockEntity(x + d.getOffsetX(), y + d.getOffsetY(), z + d.getOffsetZ());
            if (blockEntity instanceof PipeBlockEntity){
                continue;
            }
            if(!NyalibInventoryUtil.hasItemHandler(blockEntity)){
                continue;
            }
            InventoryUtil inv = this.getAdaptedInventoryUtil(NyalibInventoryUtil.getWrappedItemHandler(blockEntity, d));
            //_inventoryUtilFactory.getInventoryUtil(Utils.getInventory((IInventory) tile));
            HashMap<ItemIdentifier, Integer> currentInv = inv.getItemsAndCount();
            for (ItemIdentifier currItem : currentInv.keySet()){
                if (hasFilter()
                            && ((isExcludeFilter() && itemIsFiltered(currItem))
                                        || (!isExcludeFilter() && !itemIsFiltered(currItem)))) continue;

                if (!allItems.containsKey(currItem)){
                    allItems.put(currItem, currentInv.get(currItem));
                }else {
                    allItems.put(currItem, allItems.get(currItem) + currentInv.get(currItem));
                }
            }
        }

        //Reduce what has been reserved.
        Iterator<ItemIdentifier> iterator = allItems.keySet().iterator();
        while(iterator.hasNext()){
            ItemIdentifier item = iterator.next();

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
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Normal;
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return null;
    }

    @Override
    public void startWatching() {
        super.startWatching();
    }

    @Override
    public void stopWatching() {
        super.stopWatching();
    }

    public Inventory getInventory(Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
        if (blockEntity == null || blockEntity instanceof PipeBlockEntity) {
            return null;
        }
        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(blockEntity, ItemHandlerBlockCapability.class);
        if (capability == null) {
            return null;
        }
        ItemStack[] inventory = capability.getInventory(direction.getOpposite());
        if (inventory == null || inventory.length == 0) {
            return null;
        }
        return new ItemHandlerBlockCapabilityInventoryWrapper(capability, direction.getOpposite());
    }

    private void addToList(ItemIdentifierStack stack) {
        for(ItemIdentifierStack ident:itemList) {
            if(ident.getItem().equals(stack.getItem())) {
                ident.stackSize += stack.stackSize;
                return;
            }
        }
        itemList.addLast(stack);
    }

    private void updateInv(boolean force) {
        itemList.clear();
        for(Direction d:Direction.values()) {
            Inventory inv = getInventory(d);
            if(inv != null) {
                for(int i=0;i<inv.size();i++) {
                    if(inv.getStack(i) != null) {
                        //Filter
                        if (hasFilter()
                                    && ((isExcludeFilter() && itemIsFiltered(ItemIdentifier.get(inv.getStack(i))))
                                                || (!isExcludeFilter() && !itemIsFiltered(ItemIdentifier.get(inv.getStack(i)))))) continue;

                        addToList(ItemIdentifierStack.getFromStack(inv.getStack(i)));
                    }
                }
            }
        }
        if(!itemList.equals(oldList) || force) {
            oldList.clear();
            oldList.addAll(itemList);
//            MainProxy.sendToPlayerList(new PacketPipeInvContent(NetworkConstants.PIPE_CHEST_CONTENT, xCoord, yCoord, zCoord, itemList).getPacket(), localModeWatchers);
        }
    }


    @Override
    public void setup() {
    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    public void markInventoryDirty(){
        LinkedList<ItemIdentifierStack> all = orderManager.getContentList(world);
        if(!oldList.equals(all)) {
            oldList.clear();
            oldList.addAll(all);
//            MainProxy.sendToPlayerList(new PacketPipeInvContent(NetworkConstants.ORDER_MANAGER_CONTENT, xCoord, yCoord, zCoord, all).getPacket(), localModeWatchers);
        }
    }

    @Override
    public void setReceivedChestContent(LinkedList<ItemIdentifierStack> list) {
        itemList.clear();
        itemList.addAll(list);
    }

    @Override
    public HUDRenderer getRenderer() {
        return super.getRenderer();
    }

    @Override
    public void setOrderManagerContent(LinkedList<ItemIdentifierStack> list) {
        itemListOrderer.clear();
        itemListOrderer.addAll(list);
    }

    // Logic
    public SimpleInventory getFilterInventory() {
        return filterInventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        filterInventory.readNbt(nbt, "");
        filterIsExclude = nbt.getBoolean("filterisexclude");
        extractionMode = ExtractionMode.values()[nbt.getInt("extractionMode")];
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        filterInventory.writeNbt(nbt, "");
        nbt.putBoolean("filterisexclude", filterIsExclude);
        nbt.putInt("extractionMode", extractionMode.ordinal());
    }

    public boolean hasFilter(){
        return !dummyInvUtil.getItemsAndCount().isEmpty();
    }

    public boolean itemIsFiltered(ItemIdentifier item){
        return dummyInvUtil.getItemsAndCount().containsKey(item);
    }

    public boolean isExcludeFilter(){
        return filterIsExclude;
    }

    public void setFilterExcluded(boolean isExcluded){
        filterIsExclude = isExcluded;
    }

    public ExtractionMode getExtractionMode(){
        return extractionMode;
    }

    public void nextExtractionMode() {
        extractionMode = extractionMode.next();
    }

    public void setExtractionMode(ExtractionMode extractionMode) {
        this.extractionMode = extractionMode;
    }
}
