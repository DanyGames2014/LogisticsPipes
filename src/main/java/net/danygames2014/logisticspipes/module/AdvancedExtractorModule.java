package net.danygames2014.logisticspipes.module;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.gui.hud.modules.AdvancedExtractorHud;
import net.danygames2014.logisticspipes.gui.hud.modules.ExtractorHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.network.UpdateModuleDataS2CPacket;
import net.danygames2014.logisticspipes.network.UpdateModuleInventoryContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerModuleWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.AdvancedExtractorScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AdvancedExtractorModule implements LogisticsModule, SneakyDirectionReceiver, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, ModuleInventoryReceive, Inventory, Serializable {
    protected int currentTick = 0;
    private final SimpleInventory filterInventory = new SimpleInventory(9, "Item list", 1, this::markDirty);

    private boolean itemsIncluded = true;
    protected InventoryProvider invProvider;
    protected SendRoutedItem itemSender;
    protected WorldProvider worldProvider;
    protected SneakyDirection sneakyDirection = SneakyDirection.Default;

    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();

    private AdvancedExtractorHud HUD = new AdvancedExtractorHud(this);

    public AdvancedExtractorModule() {
    }

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
        this.itemSender = itemSender;
        this.worldProvider = world;
    }

    @Override
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("advanced_extractor");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new AdvancedExtractorScreenHandler(player, this);
    }

    public SimpleInventory getFilterInventory() {
        return filterInventory;
    }

    @Override
    public SneakyDirection getSneakyDirection() {
        return sneakyDirection;
    }

    @Override
    public void setSneakyDirection(SneakyDirection sneakyDirection) {
        this.sneakyDirection = sneakyDirection;
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {
        filterInventory.readNbt(nbt, prefix);
        setItemsIncluded(nbt.getBoolean("itemsIncluded"));
        sneakyDirection = SneakyDirection.values()[nbt.getInt("sneakydirection")];
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        filterInventory.writeNbt(nbt, prefix);
        nbt.putBoolean("itemsIncluded", areItemsIncluded());
        nbt.putInt("sneakydirection", sneakyDirection.ordinal());
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        return null;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    protected int ticksToAction() {
        return 100;
    }

    protected int itemsToExtract() {
        return 1;
    }

    @Override
    public void tick() {
        if (++currentTick < ticksToAction()) {
            return;
        }
        currentTick = 0;

        Inventory targetInventory = invProvider.getInventory();
        if (targetInventory == null) {
            return;
        }
        Direction extractDirection = switch (getSneakyDirection()) {
            case Bottom -> Direction.DOWN;
            case Top -> Direction.UP;
            case Side -> Direction.SOUTH;
            default -> invProvider.inventoryDirection().getOpposite();
        };

        if (targetInventory instanceof ItemHandlerBlockCapabilityInventoryWrapper wrapper) {
            wrapper.side = extractDirection;
        }

        ItemStack stack = checkExtract(targetInventory, true, invProvider.inventoryDirection().getOpposite());
        if (stack == null) {
            return;
        }
        itemSender.sendStack(stack);
    }

    public ItemStack checkExtract(Inventory inventory, boolean doRemove, Direction from) {
        return checkExtractGeneric(inventory, doRemove, from);
    }

    public ItemStack checkExtractGeneric(Inventory inventory, boolean doRemove, Direction from) {
        for (int k = 0; k < inventory.size(); k++) {
            if ((inventory.getStack(k) == null) || (inventory.getStack(k).count <= 0)) {
                continue;
            }

            ItemStack slot = inventory.getStack(k);
            if ((slot != null) && (slot.count > 0) && (CanExtract(slot))) {
                if (doRemove) {
                    int count = Math.min(itemsToExtract(), slot.count);

                    if(count > 0) {
                        for(int j = 0; j < count; j++){
                            if(worldProvider.getWorld().getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
                                pipe.queueParticle(ParticleColor.ORANGE, 2);
                            }
                        }
                    }

                    if (count <= 0) {
                        return null;
                    }

                    return inventory.removeStack(k, itemsToExtract());
                }
                return slot;
            }
        }
        return null;
    }

    public boolean CanExtract(ItemStack item) {
        if (!shouldSend(item)) {
            return false;
        }

        for (int i = 0; i < this.filterInventory.size(); i++) {

            ItemStack stack = this.filterInventory.getStack(i);
            if ((stack != null) && (stack.itemId == item.itemId)) {
                if (Item.ITEMS[item.itemId].isDamageable()) {
                    return areItemsIncluded();
                }
                if (stack.getDamage() == item.getDamage()) {
                    return areItemsIncluded();
                }
            }
        }
        return !areItemsIncluded();
    }

    protected boolean shouldSend(ItemStack stack) {
        return LogisticsManager.getInstance().hasDestination(worldProvider.getWorld(), stack, true, itemSender.getSourceId(), true);
    }

    public boolean areItemsIncluded() {
        return itemsIncluded;
    }

    public void setItemsIncluded(boolean flag) {
        itemsIncluded = flag;
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            PacketUtil.sendToPlayerList(new UpdateModuleDataS2CPacket(x, y, z, slot, this), localModeWatchers);
        }
    }

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        list.add(areItemsIncluded() ? "Included" : "Excluded");
        list.add("Extraction: " + sneakyDirection.name());
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

    @Override
    public void handleInvContent(LinkedList<ItemIdentifierStack> list) {
        filterInventory.handleItemStackList(list);
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
        PacketHelper.sendTo(player, new UpdateModuleDataS2CPacket(x, y, z, slot, this));
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
    public void readData(DataInputStream stream) throws IOException {
        setItemsIncluded(stream.readBoolean());
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeBoolean(areItemsIncluded());
    }
}
