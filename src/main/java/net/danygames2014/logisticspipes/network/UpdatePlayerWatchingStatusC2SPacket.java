package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.WatchingHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdatePlayerWatchingStatusC2SPacket extends CoordinatesPacket implements ManagedPacket<UpdatePlayerWatchingStatusC2SPacket> {
    private int mode;
    private boolean startWatching;

    public UpdatePlayerWatchingStatusC2SPacket() {
    }

    public UpdatePlayerWatchingStatusC2SPacket(int x, int y, int z, int mode, boolean startWatching) {
        super(x, y, z);
        this.mode = mode;
        this.startWatching = startWatching;
    }

    public static final PacketType<UpdatePlayerWatchingStatusC2SPacket> TYPE = PacketType.builder(false, true, UpdatePlayerWatchingStatusC2SPacket::new).build();

    @Override
    public @NotNull PacketType<UpdatePlayerWatchingStatusC2SPacket> getType() {
        return TYPE;
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(mode);
            stream.writeBoolean(startWatching);
        } catch (IOException ignored) {

        }
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            mode = stream.readInt();
            startWatching = stream.readBoolean();
        } catch (IOException ignored) {

        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof WatchingHandler handler) {
            if(startWatching) {
                handler.playerStartWatching(player, mode);
            } else {
                handler.playerStopWatching(player, mode);
            }
        }
    }
}
