package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DropDiskC2SPacket extends CoordinatesPacket implements ManagedPacket<DropDiskC2SPacket> {


    public static final PacketType<DropDiskC2SPacket> TYPE = PacketType.builder(false, true, DropDiskC2SPacket::new).build();

    public DropDiskC2SPacket() {
        super();
    }

    public DropDiskC2SPacket(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        super.apply(networkHandler);
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof RequestLogisticPipeBlockEntity pipe) {
            pipe.dropDisk();
        }
    }

    @Override
    public @NotNull PacketType<DropDiskC2SPacket> getType() {
        return TYPE;
    }
}
