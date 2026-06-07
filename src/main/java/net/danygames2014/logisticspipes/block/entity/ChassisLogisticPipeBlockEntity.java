package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.gui.hud.ChassisPipeHud;
import net.danygames2014.logisticspipes.gui.hud.TestHud;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.danygames2014.logisticspipes.module.ChassisModule;
import net.danygames2014.logisticspipes.network.UpdateChassisInventoryContentS2CPacket;
import net.danygames2014.logisticspipes.network.UpdatePlayerWatchingStatusC2SPacket;
import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.util.*;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class ChassisLogisticPipeBlockEntity extends LogisticPipeBlockEntity implements InventoryProvider, SendRoutedItem, ProvideItems, WorldProvider, HUDRendererProvider, SendQueueContentReceiver {
    private ChassisModule module;
    private SimpleInventory moduleInventory;
    private boolean switchOrientationOnTick = true;
    private boolean init = false;
    private long tick = 0;
    private Direction direction = Direction.UP;
    private boolean convertFromMeta = false;
    private int chassisSize = 1;

    //HUD
    public final LinkedList<ItemIdentifierStack> displayList = new LinkedList<>();
    public final PlayerCollectionList localModeWatchers = new PlayerCollectionList();
    private ChassisPipeHud HUD;

    public ChassisLogisticPipeBlockEntity() {
        super();
    }

    public ChassisLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void setup() {
//        HUD = new HUDChassiePipe(this, _module, _moduleInventory);
    }

    @Override
    public void init(BlockState state) {
        super.init(state);
        initModuleInventory(state);
    }
    
    public void initModuleInventory(BlockState state) {
        if (state.getBlock() == LogisticsPipes.chassisItemPipeMk1) {
            chassisSize = 1;
        }
        if (state.getBlock() == LogisticsPipes.chassisItemPipeMk2) {
            chassisSize = 2;
        }
        if (state.getBlock() == LogisticsPipes.chassisItemPipeMk3) {
            chassisSize = 3;
        }
        if (state.getBlock() == LogisticsPipes.chassisItemPipeMk4) {
            chassisSize = 4;
        }
        if (state.getBlock() == LogisticsPipes.chassisItemPipeMk5) {
            chassisSize = 8;
        }

        if (moduleInventory == null) {
            moduleInventory = new ChassisInventory(getChassisSize(), "Chassis pipe", 1, this::markInventoryDirty, this);
//            moduleInventory = new SimpleInventory(getChassisSize(), "Chassis pipe", 1, this::markInventoryDirty);
        }
        
        if (module == null) {
            module = new ChassisModule(getChassisSize(), this);
        }

        if(HUD == null) {
           HUD = new ChassisPipeHud(this, module, moduleInventory);
        }
    }

    public Direction getPointedDirection() {
        return direction;
    }

    public BlockEntity getPointedBlockEntity() {
        return world.getBlockEntity(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
    }

    public void nextDirection() {
        for (int l = 0; l < Direction.values().length; ++l) {
            int nextIndex = (direction.ordinal() + 1) % Direction.values().length;
            direction = Direction.values()[nextIndex];
            if (isValidDirection(direction)) {
                return;
            }
        }
    }

    public boolean isValidDirection(Direction direction) {
        if (isRoutedExit(direction)) {
            return false;
        }

        BlockEntity blockEntity = world.getBlockEntity(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());

        if (blockEntity == null) {
            return false;
        }

        return connections.get(direction) != PipeConnectionType.NONE;
    }

    @Override
    public void neighborUpdate() {
        super.neighborUpdate();
        if (!isValidDirection(direction)) {
            if (!world.isRemote) {
                nextDirection();
//                PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, DefaultProps.NETWORK_UPDATE_RANGE, MainProxy.getDimensionForWorld(worldObj), new PacketPipeUpdate(NetworkConstants.PIPE_UPDATE,xCoord,yCoord,zCoord,getLogisticsNetworkPacket()).getPacket());
            }
        }
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (wrenchMode == WrenchMode.MODE_ROTATE) {
            nextDirection();
            return true;
        }

        return super.wrenchRightClick(stack, player, isSneaking, world, x, y, z, side, wrenchMode);
    }

    @Override
    public Inventory getInventory() {
        BlockEntity blockEntity = getPointedBlockEntity();
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

    @Override
    public Direction inventoryDirection() {
        return direction;
    }

    public Inventory getModuleInventory() {
        return this.moduleInventory;
    }

    /*** ISendRoutedItem ***/

    @Override
    public void sendStack(ItemStack stack) {
        RoutedItem itemToSend = ItemUtil.createRoutedItem(stack, world);
        itemToSend.setTransportMode(RoutedItem.TransportMode.Passive);
        super.queueRoutedItem(itemToSend, getPointedDirection());
    }

    @Override
    public void sendStack(ItemStack stack, long destination) {
        RoutedItem itemToSend = ItemUtil.createRoutedItem(stack, world);
        itemToSend.setSource(getRouterId());
        itemToSend.setDestination(destination);
        itemToSend.setTransportMode(RoutedItem.TransportMode.Active);
        super.queueRoutedItem(itemToSend, getPointedDirection());
    }

    @Override
    public void sendStack(ItemStack stack, long destination, ItemSendMode mode) {
        RoutedItem itemToSend = ItemUtil.createRoutedItem(stack, world);
        itemToSend.setSource(getRouterId());
        itemToSend.setDestination(destination);
        itemToSend.setTransportMode(RoutedItem.TransportMode.Active);
        super.queueRoutedItem(itemToSend, getPointedDirection(), mode);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("chassisSize")) {
            chassisSize = nbt.getInt("chassisSize");
        }
        if (moduleInventory == null) {
            moduleInventory = new ChassisInventory(getChassisSize(), "Chassis pipe", 1, this::markInventoryDirty, this);
//            moduleInventory = new SimpleInventory(getChassisSize(), "Chassis pipe", 1, this::markInventoryDirty);
        }
        if (module == null) {
            module = new ChassisModule(getChassisSize(), this);
        }
        moduleInventory.readNbt(nbt, "chassis");
        markInventoryDirty();
        module.readNbt(nbt, "");
        if (nbt.contains("direction")) {
            direction = Direction.byId(nbt.getInt("direction"));
        }
        switchOrientationOnTick = false;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        moduleInventory.writeNbt(nbt, "chassis");
        module.writeNbt(nbt, "");
        if (direction != null) {
            nbt.putInt("direction", direction.getId());
        }
        nbt.putInt("chassisSize", getChassisSize());
    }

    @Override
    public void onBreak() {
        super.onBreak();
        if (!world.isRemote) {
            moduleInventory.dropContents(world, x, y, z);
        }
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Normal;
    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    public void markInventoryDirty() {
        boolean reInitGui = false;
        for (int i = 0; i < moduleInventory.size(); i++) {
            ItemStack stack = moduleInventory.getStack(i);
            if (stack == null) {
                if (module.hasModule(i)) {
                    module.removeModule(i);
                    reInitGui = true;
                }
                continue;
            }

            if (stack.getItem() instanceof ModuleItem moduleItem) {
                LogisticsModule current = module.getModule(i);
                LogisticsModule next = moduleItem.getLogisticsModule();
                next.registerHandler(this, this, this);
                next.registerPosition(x, y, z, i);
                if (current != next) {
                    module.installModule(i, next);
                    ModuleItem.readInformation(stack, next, this.world);
                    ModuleItem.removeInformation(stack);
                }
            }
        }
        // TODO: handle this
        if (reInitGui) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
//                if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiChassiPipe){
//                    FMLClientHandler.instance().getClient().currentScreen.initGui();
//                }
            }
        }
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            PacketUtil.sendToPlayerList(new UpdateChassisInventoryContentS2CPacket(x, y, z, true, ItemIdentifierStack.getListFromInventory(moduleInventory)), localModeWatchers);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (switchOrientationOnTick) {
            switchOrientationOnTick = false;
            if (!world.isRemote) {
                nextDirection();
//                PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, DefaultProps.NETWORK_UPDATE_RANGE, MainProxy.getDimensionForWorld(worldObj), new PacketPipeUpdate(NetworkConstants.PIPE_UPDATE,xCoord,yCoord,zCoord,getLogisticsNetworkPacket()).getPacket());
            }
        }

        if (!init) {
            init = true;
//            if(MainProxy.isClient(this.worldObj)) {
//                PacketDispatcher.sendPacketToServer(new PacketCoordinates(NetworkConstants.REQUEST_PIPE_UPDATE, xCoord, yCoord, zCoord).getPacket());
//            }
        }
    }

    public int getChassisSize() {
        return chassisSize;
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return module;
    }

    // transport layer

    @Override
    public Direction itemArrived(RoutedItem item) {
        return direction;
    }

    @Override
    public boolean stillWantItem(RoutedItem item) {
        if (module == null) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        SinkReply reply = module.sinksItem(item.getItemStack());
        if (reply == null) {
            return false;
        }

        if (reply.maxNumberOfItems != 0 && item.getItemStack().count > reply.maxNumberOfItems) {
            Direction d = direction;
            if (d == null) {
                d = Direction.UP;
            }

            RoutedItem newItem = item.split(world, reply.maxNumberOfItems, d.getOpposite());
            return false;
        }

        return module.sinksItem(item.getItemStack()) != null;
    }

    @Override
    public void canProvide(RequestTreeNode tree, Map<ItemIdentifier, Integer> donePromisses) {
        if (!isEnabled()) {
            return;
        }

        for (int i = 0; i < this.getChassisSize(); i++) {
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule) {
                ((LegacyActiveModule) x).canProvide(tree, donePromisses);
            }
        }
    }

    @Override
    public void fullFill(LogisticsPromise promise, RequestItems destination) {
        if (!isEnabled()) {
            return;
        }
        for (int i = 0; i < this.getChassisSize(); i++) {
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule) {
                ((LegacyActiveModule) x).fullFill(promise, destination);
                queueParticle(ParticleColor.WHITE, 2);
            }
        }
    }

    @Override
    public int getAvailableItemCount(ItemIdentifier item) {
        if (!isEnabled()) {
            return 0;
        }

        for (int i = 0; i < this.getChassisSize(); i++) {
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule) {
                return ((LegacyActiveModule) x).getAvailableItemCount(item);
            }
        }
        return 0;
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getAllItems() {
        if (!isEnabled()) {
            return new HashMap<>();
        }
        for (int i = 0; i < this.getChassisSize(); i++) {
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule) {
                return ((LegacyActiveModule) x).getAllItems();
            }
        }
        return new HashMap<>();
    }

    @Override
    public void handleSendQueueItemStackList(LinkedList<ItemIdentifierStack> _allItems) {
        displayList.clear();
        displayList.addAll(_allItems);
    }

    @Override
    public Router getRouter() {
        return this;
    }

    @Override
    public long getSourceId() {
        return getRouterId();
    }

    @Override
    public HUDRenderer getRenderer() {
        return HUD;
    }

    public void handleItemStackList(LinkedList<ItemIdentifierStack> allItems) {
        moduleInventory.handleItemStackList(allItems);
    }

    @Override
    public void startWatching() {
        PacketHelper.send(new UpdatePlayerWatchingStatusC2SPacket(x, y, z, 1, true));
    }

    @Override
    public void stopWatching() {
        PacketHelper.send(new UpdatePlayerWatchingStatusC2SPacket(x, y, z, 1, false));
        HUD.stopWatching();
    }

    @Override
    public void playerStartWatching(PlayerEntity player, int mode) {
        if(mode == 1) {
            localModeWatchers.add(player);
            if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                PacketHelper.sendTo(player, new UpdateChassisInventoryContentS2CPacket(x, y, z, true, ItemIdentifierStack.getListFromInventory(moduleInventory)));
                PacketHelper.sendTo(player, new UpdateChassisInventoryContentS2CPacket(x, y, z, false, displayList));
            }
        } else {
            super.playerStartWatching(player, mode);
        }
    }

    @Override
    public void playerStopWatching(PlayerEntity player, int mode) {
        super.playerStopWatching(player, mode);
        localModeWatchers.remove(player);
    }
}
