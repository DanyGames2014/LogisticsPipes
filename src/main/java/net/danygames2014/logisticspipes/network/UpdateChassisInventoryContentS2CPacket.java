package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ModuleInventoryReceive;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class UpdateChassisInventoryContentS2CPacket extends InventoryContentPacket implements ManagedPacket<UpdateChassisInventoryContentS2CPacket> {

    private boolean moduleInventory;

    public static final PacketType<UpdateChassisInventoryContentS2CPacket> TYPE = PacketType.builder(true, false, UpdateChassisInventoryContentS2CPacket::new).build();

    public UpdateChassisInventoryContentS2CPacket() {
    }

    public UpdateChassisInventoryContentS2CPacket(int x, int y, int z, boolean moduleInventory, LinkedList<ItemIdentifierStack> allItems) {
        super(x, y, z, allItems);
        this.moduleInventory = moduleInventory;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            moduleInventory = stream.readBoolean();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeBoolean(moduleInventory);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof ChassisLogisticPipeBlockEntity pipe) {
            if(moduleInventory) {
                pipe.handleItemStackList(allItems);
            } else {
                pipe.handleSendQueueItemStackList(allItems);
            }
        }
    }

    @Override
    public @NotNull PacketType<UpdateChassisInventoryContentS2CPacket> getType() {
        return TYPE;
    }
}
