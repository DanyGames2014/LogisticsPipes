package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.screen.handler.SatelliteScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UnknownFormatConversionException;

public class SatelliteIdC2SPacket extends Packet implements ManagedPacket<SatelliteIdC2SPacket> {
    public static final PacketType<SatelliteIdC2SPacket> TYPE = PacketType.builder(false, true, SatelliteIdC2SPacket::new).build();

    // 0 = Increment ID, 1 = Decrement ID
    private int command;

    public SatelliteIdC2SPacket(int command) {
        this.command = command;
    }

    public SatelliteIdC2SPacket() {

    }

    @Override
    public void read(DataInputStream stream) {
        try {
            command = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (player.currentScreenHandler instanceof SatelliteScreenHandler satelliteScreenHandler) {
            switch (command) {
                case 0 -> {
                    satelliteScreenHandler.satellite.setNextId(player);
                }
                case 1 -> {
                    satelliteScreenHandler.satellite.setPrevId(player);
                }
                default -> {
                    LogisticsPipes.LOGGER.error("Unknown satellite command: " + command + " from player " + player.name);
                }
            }
        }
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public @NotNull PacketType<SatelliteIdC2SPacket> getType() {
        return TYPE;
    }
}
