package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.packet.UpdatePacket;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
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

public class UpdatePipeDataS2CPacket extends UpdatePacket implements ManagedPacket<UpdatePipeDataS2CPacket> {

    private int x;
    private int y;
    private int z;

    public static final PacketType<UpdatePipeDataS2CPacket> TYPE = PacketType.builder(true, false, UpdatePipeDataS2CPacket::new).build();

    public UpdatePipeDataS2CPacket() {
    }

    public UpdatePipeDataS2CPacket(int x, int y, int z, Serializable serializable) {
        super(serializable);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readIdentificationData(DataInputStream stream) {
        try {
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
        if(player.world.getBlockEntity(x, y, z) instanceof Serializable serializable) {
            DataInputStream playbackStream = new DataInputStream(new ByteArrayInputStream(dataBuffer));
            try {
                serializable.readData(playbackStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public @NotNull PacketType<UpdatePipeDataS2CPacket> getType() {
        return TYPE;
    }
}
