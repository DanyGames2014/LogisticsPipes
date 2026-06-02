package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.buildcraft.packet.UpdatePacket;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ModuleInventoryReceive;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateModuleDataS2CPacket extends UpdatePacket implements ManagedPacket<UpdateModuleDataS2CPacket> {

    private int slot;
    private int x;
    private int y;
    private int z;

    public static final PacketType<UpdateModuleDataS2CPacket> TYPE = PacketType.builder(true, false, UpdateModuleDataS2CPacket::new).build();

    public UpdateModuleDataS2CPacket() {
    }

    public UpdateModuleDataS2CPacket(int x, int y, int z, int slot, Serializable serializable) {
        super(serializable);
        this.slot = slot;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readIdentificationData(DataInputStream stream) {
        try {
            slot = stream.readInt();
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeIdentificationData(DataOutputStream stream) {
        try {
            stream.writeInt(slot);
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof ChassisLogisticPipeBlockEntity pipe && pipe.getLogisticsModule() != null && pipe.getLogisticsModule().getSubModule(slot) instanceof Serializable serializable) {
            DataInputStream playbackStream = new DataInputStream(new ByteArrayInputStream(dataBuffer));
            try {
                serializable.readData(playbackStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public @NotNull PacketType<UpdateModuleDataS2CPacket> getType() {
        return TYPE;
    }
}
