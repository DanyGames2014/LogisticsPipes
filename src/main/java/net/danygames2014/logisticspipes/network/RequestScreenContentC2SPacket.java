package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestScreenContentC2SPacket extends CoordinatesPacket implements ManagedPacket<RequestScreenContentC2SPacket> {

    RequestHandler.DisplayOptions option;

    public static final PacketType<RequestScreenContentC2SPacket> TYPE = PacketType.builder(false, true, RequestScreenContentC2SPacket::new).build();

    public RequestScreenContentC2SPacket() {
        super();
    }

    public RequestScreenContentC2SPacket(int x, int y, int z, RequestHandler.DisplayOptions option) {
        super(x, y, z);
        this.option = option;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            option = RequestHandler.DisplayOptions.values()[stream.readInt()];
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(option.ordinal());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        super.apply(networkHandler);
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
            RequestHandler.refresh(player, pipe, option);
        }
    }

    @Override
    public @NotNull PacketType<RequestScreenContentC2SPacket> getType() {
        return TYPE;
    }
}
