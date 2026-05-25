package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RequestDiskContentC2SPacket extends CoordinatesPacket implements ManagedPacket<RequestDiskContentC2SPacket> {


    public static final PacketType<RequestDiskContentC2SPacket> TYPE = PacketType.builder(false, true, RequestDiskContentC2SPacket::new).build();

    public RequestDiskContentC2SPacket() {
        super();
    }

    public RequestDiskContentC2SPacket(int x, int y, int z) {
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
            PacketHelper.sendTo(player, new SendDiskContentS2CPacket(x, y, z, pipe.getDiskInventory().getStack(0)));
        }
    }

    @Override
    public @NotNull PacketType<RequestDiskContentC2SPacket> getType() {
        return TYPE;
    }
}
