package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.item.ModuleItem;
import net.danygames2014.logisticspipes.module.ChassisModule;
import net.danygames2014.logisticspipes.request.RequestTreeNode;
import net.danygames2014.logisticspipes.routing.LogisticsPromise;
import net.danygames2014.logisticspipes.routing.Router;
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
    public final List<PlayerEntity> localModeWatchers = new ArrayList<>();
//    private HUDChassiePipe HUD;

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
    public void init(BlockState blockState) {
        super.init(blockState);
        if(blockState.getBlock() == LogisticsPipes.chassisItemPipeMk1){
            chassisSize = 1;
        }
        if(blockState.getBlock() == LogisticsPipes.chassisItemPipeMk2){
            chassisSize = 2;
        }
        if(blockState.getBlock() == LogisticsPipes.chassisItemPipeMk3){
            chassisSize = 3;
        }
        if(blockState.getBlock() == LogisticsPipes.chassisItemPipeMk4){
            chassisSize = 4;
        }
        if(blockState.getBlock() == LogisticsPipes.chassisItemPipeMk5){
            chassisSize = 8;
        }

        moduleInventory = new SimpleInventory(getChassisSize(), "Chassis pipe", 1, this::markInventoryDirty);
        module = new ChassisModule(getChassisSize(), this);
    }

    public Direction getPointedDirection(){
        return direction;
    }

    public BlockEntity getPointedBlockEntity(){
        Position position = new Position(x, y, z, direction);
        position.moveForwards(1.0);
        return world.getBlockEntity((int)position.x, (int)position.y, (int)position.z);
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

    public boolean isValidDirection(Direction direction){
        if(isRoutedExit(direction)){
            return false;
        }
        Position position = new Position(x, y, z, direction);
        position.moveForwards(1.0);
        BlockEntity blockEntity = world.getBlockEntity((int)position.x, (int)position.y, (int)position.z);
        if(blockEntity == null){
            return false;
        }
        return connections.get(direction) != PipeConnectionType.NONE;
    }

    @Override
    public void neighborUpdate() {
        super.neighborUpdate();
        if(!isValidDirection(direction)){
            if(!world.isRemote){
                nextDirection();
//                PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, DefaultProps.NETWORK_UPDATE_RANGE, MainProxy.getDimensionForWorld(worldObj), new PacketPipeUpdate(NetworkConstants.PIPE_UPDATE,xCoord,yCoord,zCoord,getLogisticsNetworkPacket()).getPacket());
            }
        }
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if(super.wrenchRightClick(stack, player, isSneaking, world, x, y, z, side, wrenchMode)){
            return true;
        }
        if(wrenchMode == WrenchMode.MODE_WRENCH && isSneaking){
            nextDirection();
            return true;
        }
        return false;
    }

    @Override
    public Inventory getInventory() {
        BlockEntity blockEntity = getPointedBlockEntity();
        if(blockEntity == null || blockEntity instanceof PipeBlockEntity){
            return null;
        }
        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(blockEntity, ItemHandlerBlockCapability.class);
        if(capability == null){
            return null;
        }
        ItemStack[] inventory = capability.getInventory(direction.getOpposite());
        if(inventory == null || inventory.length == 0){
            return null;
        }
        return new ItemHandlerBlockCapabilityInventoryWrapper(capability, direction.getOpposite());
    }

    @Override
    public Direction inventoryDirection() {
        return direction;
    }

    public Inventory getModuleInventory(){
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
        moduleInventory.readNbt(nbt, "chassis");
        markInventoryDirty();
        module.readNbt(nbt, "");
        if(nbt.contains("direction")){
            direction = Direction.byId(nbt.getInt("direction"));
        }
        switchOrientationOnTick = false;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        moduleInventory.writeNbt(nbt, "chassis");
        module.writeNbt(nbt, "");
        if(direction != null){
            nbt.putInt("direction", direction.getId());
        }
    }

    @Override
    public void onBreak() {
        super.onBreak();
        if(!world.isRemote){
            moduleInventory.dropContents(world, x, y , z);
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
        for (int i = 0; i < moduleInventory.size(); i++){
            ItemStack stack = moduleInventory.getStack(i);
            if (stack == null){
                if (module.hasModule(i)){
                    module.removeModule(i);
                    reInitGui = true;
                }
                continue;
            }

            if (stack.getItem() instanceof ModuleItem moduleItem){
                LogisticsModule current = module.getModule(i);
                LogisticsModule next = moduleItem.getLogisticsModule();
                next.registerHandler(this, this, this);
                next.registerPosition(x, y, z, i);
                if (current != next){
                    module.installModule(i, next);
                    ModuleItem.readInformation(stack, next, this.world);
                    ModuleItem.removeInformation(stack);
                }
            }
        }
        // TODO: handle this
        if (reInitGui) {
            if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
//                if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiChassiPipe){
//                    FMLClientHandler.instance().getClient().currentScreen.initGui();
//                }
            }
        }
//        if(MainProxy.isServer()) {
//            MainProxy.sendToPlayerList(new PacketPipeInvContent(NetworkConstants.CHASSIE_PIPE_MODULE_CONTENT, xCoord, yCoord, zCoord, ItemIdentifierStack.getListFromInventory(_moduleInventory)).getPacket(), localModeWatchers);
//        }
    }

    @Override
    public void tick() {
        super.tick();
        if(switchOrientationOnTick){
            switchOrientationOnTick = false;
            if(!world.isRemote){
                nextDirection();
//                PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, DefaultProps.NETWORK_UPDATE_RANGE, MainProxy.getDimensionForWorld(worldObj), new PacketPipeUpdate(NetworkConstants.PIPE_UPDATE,xCoord,yCoord,zCoord,getLogisticsNetworkPacket()).getPacket());
            }
        }

        if(!init) {
            init = true;
//            if(MainProxy.isClient(this.worldObj)) {
//                PacketDispatcher.sendPacketToServer(new PacketCoordinates(NetworkConstants.REQUEST_PIPE_UPDATE, xCoord, yCoord, zCoord).getPacket());
//            }
        }
    }

    public int getChassisSize(){
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
        if(module == null){
            return false;
        }
        if(!isEnabled()){
            return false;
        }
        SinkReply reply = module.sinksItem(item.getItemStack());
        if(reply == null){
            return false;
        }

        if(reply.maxNumberOfItems != 0 && item.getItemStack().count > reply.maxNumberOfItems){
            Direction d = direction;
            if(d == null){
                d = Direction.UP;
            }

            RoutedItem newItem = item.split(world, reply.maxNumberOfItems, d.getOpposite());
            return false;
        }

        return module.sinksItem(item.getItemStack()) != null;
    }


    // TODO: this should be wrench use
//    @Override
//    public boolean blockActivated(World world, int i, int j, int k,	EntityPlayer entityplayer) {
//        if (entityplayer.getCurrentEquippedItem() == null) return super.blockActivated(world, i, j, k, entityplayer);
//
//        if (entityplayer.getCurrentEquippedItem().getItem() == buildcraft.BuildCraftCore.wrenchItem){
//            if (entityplayer.isSneaking()){
//                ((PipeLogisticsChassi)this.container.pipe).nextOrientation();
//                return true;
//            }
//        }
//        return super.blockActivated(world, i, j, k, entityplayer);
//    }


    @Override
    public void canProvide(RequestTreeNode tree, Map<ItemIdentifier, Integer> donePromisses) {
        if (!isEnabled()){
            return;
        }

        for (int i = 0; i < this.getChassisSize(); i++){
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule){
                ((LegacyActiveModule)x).canProvide(tree, donePromisses);
            }
        }
    }

    @Override
    public void fullFill(LogisticsPromise promise, RequestItems destination) {
        if (!isEnabled()){
            return;
        }
        for (int i = 0; i < this.getChassisSize(); i++){
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule){
                ((LegacyActiveModule)x).fullFill(promise, destination);
            }
        }
    }

    @Override
    public int getAvailableItemCount(ItemIdentifier item) {
        if (!isEnabled()){
            return 0;
        }

        for (int i = 0; i < this.getChassisSize(); i++){
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule){
                return ((LegacyActiveModule)x).getAvailableItemCount(item);
            }
        }
        return 0;
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getAllItems() {
        if (!isEnabled()){
            return new HashMap<>();
        }
        for (int i = 0; i < this.getChassisSize(); i++){
            LogisticsModule x = module.getSubModule(i);
            if (x instanceof LegacyActiveModule){
                return ((LegacyActiveModule)x).getAllItems();
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
}
