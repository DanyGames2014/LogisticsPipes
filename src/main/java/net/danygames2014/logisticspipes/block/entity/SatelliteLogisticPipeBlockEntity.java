package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.*;
import net.danygames2014.logisticspipes.request.RequestManager;
import net.danygames2014.logisticspipes.routing.LogisticsNetwork;
import net.danygames2014.logisticspipes.routing.LogisticsNetworkManager;
import net.danygames2014.logisticspipes.util.ItemHandlerBlockCapabilityInventoryWrapper;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class SatelliteLogisticPipeBlockEntity extends LogisticPipeBlockEntity implements RequestItems, HUDRendererProvider, ChestContentReceiver, RequireReliableTransport {
    public final List<PlayerEntity> localModeWatchers = new ArrayList<>();
    public final LinkedList<ItemIdentifierStack> itemList = new LinkedList<>();
    public final LinkedList<ItemIdentifierStack> oldList = new LinkedList<>();
//    private final HUDSatellite HUD = new HUDSatellite(this);

    public static HashSet<SatelliteLogisticPipeBlockEntity> AllSatellites = new HashSet<>();

    protected final LinkedList<ItemIdentifier> lostItems = new LinkedList<>();

    public int satelliteId;

    public SatelliteLogisticPipeBlockEntity() {
        super();
    }

    public SatelliteLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void setup() {
        throttleTime = 40;
    }

    @Override
    public void tick() {
        super.tick();
        if(world.isRemote) {
            return;
        }
        if(world.getTime() % 20 == 0) {
            updateInv(false);
        }
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return null;
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Normal;
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
        for(Direction direction : Direction.values()) {
            Inventory inv = getInventory(direction);
            if(inv != null) {
                for(int i=0;i<inv.size();i++) {
                    if(inv.getStack(i) != null) {
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
    public void setReceivedChestContent(LinkedList<ItemIdentifierStack> list) {
        itemList.clear();
        itemList.addAll(list);
    }

    @Override
    public HUDRenderer getRenderer() {
        return super.getRenderer();
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        satelliteId = nbt.getInt("satelliteid");
        ensureAllSatelliteStatus();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("satelliteid", satelliteId);
    }

    protected int findId(int increment) {
        if(world.isRemote) return satelliteId;
        int potentialId = satelliteId;
        boolean conflict = true;
        while (conflict) {
            potentialId += increment;
            if (potentialId < 0) {
                return 0;
            }
            conflict = false;
            for (final SatelliteLogisticPipeBlockEntity sat : AllSatellites) {
                if (sat.satelliteId == potentialId) {
                    conflict = true;
                    break;
                }
            }
        }
        return potentialId;
    }

    protected void ensureAllSatelliteStatus() {
        if(world.isRemote) return;
        if (satelliteId == 0 && AllSatellites.contains(this)) {
            AllSatellites.remove(this);
        }
        if (satelliteId != 0 && !AllSatellites.contains(this)) {
            AllSatellites.add(this);
        }
    }

    public void setNextId(PlayerEntity player) {
        satelliteId = findId(1);
        ensureAllSatelliteStatus();
        if (world.isRemote) {
//            final PacketCoordinates packet = new PacketCoordinates(NetworkConstants.SATELLITE_PIPE_NEXT, xCoord, yCoord, zCoord);
//            PacketDispatcher.sendPacketToServer(packet.getPacket());
        } else {
//            final PacketPipeInteger packet = new PacketPipeInteger(NetworkConstants.SATELLITE_PIPE_SATELLITE_ID, xCoord, yCoord, zCoord, satelliteId);
//            PacketDispatcher.sendPacketToPlayer(packet.getPacket(), (Player)player);
        }
        updateWatchers();
    }

    public void setPrevId(PlayerEntity player) {
        satelliteId = findId(-1);
        ensureAllSatelliteStatus();
        if (world.isRemote) {
//            final PacketCoordinates packet = new PacketCoordinates(NetworkConstants.SATELLITE_PIPE_PREV, xCoord, yCoord, zCoord);
//            PacketDispatcher.sendPacketToServer(packet.getPacket());
        } else {
//            final PacketPipeInteger packet = new PacketPipeInteger(NetworkConstants.SATELLITE_PIPE_SATELLITE_ID, xCoord, yCoord, zCoord, satelliteId);
//            PacketDispatcher.sendPacketToPlayer(packet.getPacket(),(Player) player);
        }
        updateWatchers();
    }

    private void updateWatchers() {
//        for(EntityPlayer player : ((PipeItemsSatelliteLogistics)this.container.pipe).localModeWatchers) {
//            final PacketPipeInteger packet = new PacketPipeInteger(NetworkConstants.SATELLITE_PIPE_SATELLITE_ID, xCoord, yCoord, zCoord, satelliteId);
//            PacketDispatcher.sendPacketToPlayer(packet.getPacket(),(Player) player);
//        }
    }

    @Override
    public void onBreak() {
        super.onBreak();
        if(world.isRemote){
            return;
        }
        AllSatellites.remove(this);
    }

    @Override
    public void throttledUpdateEntity() {
        super.throttledUpdateEntity();
        if(lostItems.isEmpty()) {
            return;
        }
        lostItems.removeIf(itemIdentifier -> RequestManager.request(itemIdentifier.makeStack(1), this, LogisticsNetworkManager.fetchRoutersByMetric(world, this), null));
    }

    @Override
    public void itemLost(ItemIdentifier item) {
        lostItems.add(item);
    }

    @Override
    public void itemArrived(ItemIdentifier item) {
    }

    public void setSatelliteId(int integer) {
        satelliteId = integer;
    }
}
