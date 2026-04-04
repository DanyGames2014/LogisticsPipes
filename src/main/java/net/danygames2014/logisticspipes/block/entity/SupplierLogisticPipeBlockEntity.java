package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.RedstoneEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.interfaces.RequireReliableTransport;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.danygames2014.logisticspipes.request.RequestManager;
import net.danygames2014.logisticspipes.util.*;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.HashMap;

public class SupplierLogisticPipeBlockEntity extends LogisticPipeBlockEntity implements RequireReliableTransport, RequestItems {
    private SimpleInventory dummyInventory;
    private InventoryUtil dummyInvUtil;

    private final HashMap<ItemIdentifier, Integer> requestedItems = new HashMap<>();

    private boolean requestPartials = false;

    public boolean pause = false;

    private boolean lastRequestFailed = false;

    public SupplierLogisticPipeBlockEntity() {
        super();
    }

    public SupplierLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    /* TRIGGER INTERFACE */
    public boolean isRequestFailed(){
        return lastRequestFailed;
    }

    public void setRequestFailed(boolean value){
        lastRequestFailed = value;
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Normal;
    }

    @Override
    public void throttledUpdateEntity() {
        if(!isEnabled()){
            return;
        }

        if(world.isRemote){
            return;
        }
        if(pause) {
            return;
        }

        for(AdjacentBlockEntity adjacent : WorldUtil.getAdjacentBlockEntities(world, x, y, z)){
            if(adjacent.blockEntity instanceof PipeBlockEntity){
                continue;
            }
            ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(adjacent.blockEntity, ItemHandlerBlockCapability.class);
            if(capability == null){
                continue;
            }

            //Do not attempt to supply redstone engines
            if(adjacent.blockEntity instanceof RedstoneEngineBlockEntity){
                continue;
            }

            Inventory inventory = new ItemHandlerBlockCapabilityInventoryWrapper(capability, adjacent.direction);
            if(inventory.size() < 1) {
                continue;
            }

            InventoryUtil invUtil = new InventoryUtil(inventory, false);

            //How many do I want?
            HashMap<ItemIdentifier, Integer> needed = dummyInvUtil.getItemsAndCount();

            //How many do I have?
            HashMap<ItemIdentifier, Integer> have = invUtil.getItemsAndCount();

            //Reduce what I have
            for (ItemIdentifier item : needed.keySet()){
                if (have.containsKey(item)){
                    needed.put(item, needed.get(item) - have.get(item));
                }
            }

            //Reduce what have been requested already
            for (ItemIdentifier item : needed.keySet()){
                if (requestedItems.containsKey(item)){
                    needed.put(item, needed.get(item) - requestedItems.get(item));
                }
            }

            setRequestFailed(false);

            for (ItemIdentifier need : needed.keySet()){
                if (needed.get(need) < 1) continue;
                int neededCount = needed.get(need);
                boolean success;
                do{
                    success = RequestManager.request(need.makeStack(neededCount),  this, RoutingUtil.getRouterListFromMap(routingTable), null);
                    if (success || neededCount == 1){
                        break;
                    }
                    neededCount = neededCount / 2;
                } while (requestPartials);

                if (success){
                    if (!requestedItems.containsKey(need)){
                        requestedItems.put(need, neededCount);
                    }else
                    {
                        requestedItems.put(need, requestedItems.get(need) + neededCount);
                    }
                } else{
                    setRequestFailed(true);
                }

            }
        }
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return null;
    }

    @Override
    public void setup() {

    }

    @Override
    public void init() {
        dummyInventory = new SimpleInventory(9, "Items to keep stocked", 127, this::markDirty);
        dummyInvUtil = new InventoryUtil(dummyInventory, false);
        throttleTime = 100;
    }

    /*** GUI ***/
    public SimpleInventory getDummyInventory() {
        return dummyInventory;
    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        dummyInventory.readNbt(nbt, "");
        requestPartials = nbt.getBoolean("requestpartials");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        dummyInventory.writeNbt(nbt, "");
        nbt.putBoolean("requestpartials", requestPartials);
    }

    @Override
    public void itemLost(ItemIdentifier item) {
        if (requestedItems.containsKey(item)){
            requestedItems.put(item, requestedItems.get(item) - 1);
        }
    }

    @Override
    public void itemArrived(ItemIdentifier item) {
        super.resetThrottle();
        if (requestedItems.containsKey(item)){
            requestedItems.put(item, requestedItems.get(item) - 1);
        }
    }

    public boolean isRequestingPartials(){
        return requestPartials;
    }

    public void setRequestingPartials(boolean value){
        requestPartials = value;
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if(wrenchMode == WrenchMode.MODE_WRENCH && !isSneaking){
            GuiHelper.openGUI(player, LogisticsPipes.NAMESPACE.id("supplier"), this, null);
            return true;
        }
        return false;
    }
}
