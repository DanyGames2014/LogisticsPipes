package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ModuleWatchReceiver;
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

public class UpdatePlayerModuleWatchingStatusC2SPacket extends CoordinatesPacket implements ManagedPacket<UpdatePlayerModuleWatchingStatusC2SPacket> {
    private int slot;
    private boolean startWatching;

    public UpdatePlayerModuleWatchingStatusC2SPacket() {
    }

    public UpdatePlayerModuleWatchingStatusC2SPacket(int x, int y, int z, int slot, boolean startWatching) {
        super(x, y, z);
        this.slot = slot;
        this.startWatching = startWatching;
    }

    public static final PacketType<UpdatePlayerModuleWatchingStatusC2SPacket> TYPE = PacketType.builder(false, true, UpdatePlayerModuleWatchingStatusC2SPacket::new).build();

    @Override
    public @NotNull PacketType<UpdatePlayerModuleWatchingStatusC2SPacket> getType() {
        return TYPE;
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(slot);
            stream.writeBoolean(startWatching);
        } catch (IOException ignored) {

        }
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            slot = stream.readInt();
            startWatching = stream.readBoolean();
        } catch (IOException ignored) {

        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof ChassisLogisticPipeBlockEntity pipe && pipe.getLogisticsModule() != null && pipe.getLogisticsModule().getSubModule(slot) instanceof ModuleWatchReceiver receiver) {
            if(startWatching) {
                receiver.startWatching(player);
            } else {
                receiver.stopWatching(player);
            }
        }
    }
}
