package net.danygames2014.logisticspipes.entity;

import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.RequireReliableTransport;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.routing.Router;
import net.danygames2014.logisticspipes.util.RoutingUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class RoutedItemEntity extends TravellingItemEntity implements RoutedItem {
    public Long sourceUUID;
    public Long destinationUUID;

    private boolean doNotBuffer;

    public boolean arrived;

    private TransportMode transportMode = TransportMode.Unknown;

    private List<PlayerEntity> knownBy = new ArrayList<>();

    public RoutedItemEntity(World world, TravellingItemEntity itemEntity){
        super(world, itemEntity.x, itemEntity.y, itemEntity.z, itemEntity.stack);
        this.transporter = itemEntity.transporter;
        this.input = itemEntity.input;
        this.travelDirection = itemEntity.travelDirection;
        this.lastTravelDirection = itemEntity.lastTravelDirection;
        this.speed = itemEntity.speed;
        this.lastSpeed = itemEntity.lastSpeed;
        this.invalidTimer = itemEntity.invalidTimer;
        this.toMiddle = itemEntity.toMiddle;

    }

    public RoutedItemEntity(World world, double x, double y, double z) {
        super(world, x, y, z, null);
    }

    @Override
    public void drop() {
        if(world.isRemote) return;
        if(sourceUUID != null){
            Router source = RoutingUtil.getRouter(world, sourceUUID);
            if(source != null){
                source.itemDropped(this);
            }
        }

        if(destinationUUID != null){
            Router destination = RoutingUtil.getRouter(world, sourceUUID);
            if(destination != null){
                destination.itemDropped(this);
                if(!arrived && destination.getPipe() != null && destination.getPipe() instanceof RequireReliableTransport reliableTransport){
                    reliableTransport.itemLost(stack);
                }
            }
        }
        super.drop();
    }

    @Override
    public Long getDestination() {
        return destinationUUID;
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public void setDestination(Long destination) {
        this.destinationUUID = destination;
        knownBy.clear();
    }

    @Override
    public Long getSource() {
        return sourceUUID;
    }

    @Override
    public void setSource(Long source) {
        this.sourceUUID = source;
        knownBy.clear();
    }

    @Override
    public void setDoNotBuffer(boolean isBuffered) {
        doNotBuffer = isBuffered;
    }

    @Override
    public boolean getDoNotBuffer() {
        return doNotBuffer;
    }

    @Override
    public TravellingItemEntity getTravellingItemEntity() {
        return this;
    }

    @Override
    public RoutedItem split(World world, int itemsToTake, Direction direction) {
        RoutedItemEntity newItem = new RoutedItemEntity(world, x, y, z);
        newItem.speed = this.speed;
        newItem.stack = this.stack.split(itemsToTake);

        if(transporter.blockEntity instanceof ChassisLogisticPipeBlockEntity chassis){
            chassis.queueRoutedItem(newItem, direction.getOpposite());
        }
        transporter.receiveTravellingItem(newItem, direction);

        world.spawnEntity(newItem);

        knownBy.clear();

        return newItem;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        if(knownBy != null) {
            knownBy.clear();
        }
    }

    @Override
    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = transportMode;
    }

    @Override
    public TransportMode getTransportMode() {
        return this.transportMode;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if(nbt.contains("sourceUUID")) {
            sourceUUID = nbt.getLong("sourceUUID");
        }
        if(nbt.contains("destinationUUID")) {
            destinationUUID = nbt.getLong("destinationUUID");
        }
        arrived = nbt.getBoolean("arrived");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if(sourceUUID != null){
            nbt.putLong("sourceUUID", sourceUUID);
        }
        if(destinationUUID != null){
            nbt.putLong("destinationUUID", destinationUUID);
        }
        nbt.putBoolean("arrived", arrived);
    }
}
