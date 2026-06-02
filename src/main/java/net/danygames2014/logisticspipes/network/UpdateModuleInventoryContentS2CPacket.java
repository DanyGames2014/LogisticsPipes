package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ModuleInventoryReceive;
import net.danygames2014.logisticspipes.interfaces.ModuleWatchReceiver;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.SendNbtCompound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class UpdateModuleInventoryContentS2CPacket extends InventoryContentPacket implements ManagedPacket<UpdateModuleInventoryContentS2CPacket> {

    private int slot;

    public static final PacketType<UpdateModuleInventoryContentS2CPacket> TYPE = PacketType.builder(true, false, UpdateModuleInventoryContentS2CPacket::new).build();

    public UpdateModuleInventoryContentS2CPacket() {
    }

    public UpdateModuleInventoryContentS2CPacket(int x, int y, int z, int slot, LinkedList<ItemIdentifierStack> allItems) {
        super(x, y, z, allItems);
        this.slot = slot;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            slot = stream.readInt();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(slot);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof ChassisLogisticPipeBlockEntity pipe && pipe.getLogisticsModule() != null && pipe.getLogisticsModule().getSubModule(slot) instanceof ModuleInventoryReceive receiver) {
            receiver.handleInvContent(allItems);
        }
    }

    @Override
    public @NotNull PacketType<UpdateModuleInventoryContentS2CPacket> getType() {
        return TYPE;
    }
}
