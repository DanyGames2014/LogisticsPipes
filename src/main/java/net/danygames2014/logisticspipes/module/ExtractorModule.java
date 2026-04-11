package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.util.ItemHandlerBlockCapabilityInventoryWrapper;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.danygames2014.logisticspipes.util.SneakyDirection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class ExtractorModule implements LogisticsModule, SneakyDirectionReceiver, ClientInformationProvider, HUDModuleHandler, ModuleWatchReceiver {

    private int currentTick;

    private InventoryProvider invProvider;
    private SendRoutedItem itemSender;
    private WorldProvider worldProvider;
    private SneakyDirection sneakyDirection = SneakyDirection.Default;

    private int slot = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private final List<PlayerEntity> localModeWatchers = new ArrayList<>();

    public ExtractorModule(){}

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

    protected int ticksToAction(){
        return 100;
    }

    protected int itemsToExtract(){
        return 1;
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
        if(++currentTick < ticksToAction()){
            return;
        }
        currentTick = 0;

        //Extract Item
        Inventory targetInventory = invProvider.getInventory();
        if(targetInventory == null){
            return;
        }
        Direction extractDirection = switch (getSneakyDirection()) {
            case Bottom -> Direction.DOWN;
            case Top -> Direction.UP;
            case Side -> Direction.SOUTH;
            default -> invProvider.inventoryDirection().getOpposite();
        };

        if(targetInventory instanceof ItemHandlerBlockCapabilityInventoryWrapper wrapper) {
            wrapper.side = extractDirection;
        }

        ItemStack stackToSend;

        for(int i = 0; i < targetInventory.size(); i++) {
            stackToSend = targetInventory.getStack(i);
            if(stackToSend == null) {
                continue;
            }
            if(!this.shouldSend(stackToSend)){
                continue;
            }

            int count = Math.min(itemsToExtract(), stackToSend.count);

            if(count <= 0) {
                break;
            }

            stackToSend = targetInventory.removeStack(i, count);
            itemSender.sendStack(stackToSend);
            break;
        }
    }

    protected boolean shouldSend(ItemStack stack){
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

    }

    @Override
    public void stopWatching() {

    }

    @Override
    public void startWatching(PlayerEntity player) {

    }

    @Override
    public void stopWatching(PlayerEntity player) {

    }

    @Override
    public HUDModuleRenderer getRenderer() {
        return null;
    }
}
