package net.danygames2014.logisticspipes.module;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.gui.hud.modules.ExtractorHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.network.UpdateModuleDataS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerModuleWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.screen.handler.ExtractorScreenHandler;
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
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractorModule implements LogisticsModule, SneakyDirectionReceiver, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver, Inventory, Serializable {

    private int currentTick;

    private InventoryProvider invProvider;
    private SendRoutedItem itemSender;
    private WorldProvider worldProvider;
    private SneakyDirection sneakyDirection = SneakyDirection.Default;

    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();

    private ExtractorHud HUD = new ExtractorHud(this);

    public ExtractorModule() {
    }

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
        this.itemSender = itemSender;
        this.worldProvider = world;
    }

    @Override
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("extractor");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new ExtractorScreenHandler(player, this);
    }

    protected int ticksToAction() {
        return 100;
    }

    protected int itemsToExtract() {
        return 1;
    }

    @Override
    public SneakyDirection getSneakyDirection() {
        return sneakyDirection;
    }

    @Override
    public void setSneakyDirection(SneakyDirection sneakyDirection) {
        this.sneakyDirection = sneakyDirection;
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            PacketUtil.sendToPlayerList(new UpdateModuleDataS2CPacket(x, y, z, slot, this), localModeWatchers);
        }
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
    public void readNbt(NbtCompound nbt, String prefix) {
        sneakyDirection = SneakyDirection.values()[nbt.getInt("sneakydirection")];
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        nbt.putInt("sneakydirection", sneakyDirection.ordinal());
    }

    @Override
    public void tick() {
        if (++currentTick < ticksToAction()) {
            return;
        }
        currentTick = 0;

        //Extract Item
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

        ItemStack stackToSend;

        for (int i = 0; i < targetInventory.size(); i++) {
            stackToSend = targetInventory.getStack(i);
            if (stackToSend == null) {
                continue;
            }
            if (!this.shouldSend(stackToSend)) {
                continue;
            }

            int count = Math.min(itemsToExtract(), stackToSend.count);

            if (count <= 0) {
                break;
            }

            stackToSend = targetInventory.removeStack(i, count);
            itemSender.sendStack(stackToSend);
            break;
        }
    }

    protected boolean shouldSend(ItemStack stack) {
        return LogisticsManager.getInstance().hasDestination(worldProvider.getWorld(), stack, true, itemSender.getSourceId(), true);
    }

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        list.add("Extraction: " + sneakyDirection.name());
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

    // Inventory
    @Override
    public int size() {
        return 0;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getMaxCountPerStack() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        setSneakyDirection(SneakyDirection.values()[stream.readInt()]);
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeInt(getSneakyDirection().ordinal());
    }
}
