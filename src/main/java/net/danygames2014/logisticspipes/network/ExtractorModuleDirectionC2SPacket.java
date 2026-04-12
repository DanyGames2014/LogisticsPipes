package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.screen.handler.ExtractorScreenHandler;
import net.danygames2014.logisticspipes.util.SneakyDirection;
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

public class ExtractorModuleDirectionC2SPacket extends Packet implements ManagedPacket<ExtractorModuleDirectionC2SPacket> {
    public static final PacketType<ExtractorModuleDirectionC2SPacket> TYPE = PacketType.builder(false, true, ExtractorModuleDirectionC2SPacket::new).build();

    private int direction;

    public ExtractorModuleDirectionC2SPacket(int direction) {
        this.direction = direction;
    }

    public ExtractorModuleDirectionC2SPacket() {

    }

    @Override
    public void read(DataInputStream stream) {
        try {
            direction = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(direction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (player.currentScreenHandler instanceof ExtractorScreenHandler extractorScreenHandler) {
            if (direction < 0 || direction > 3) {
                return;
            }

            extractorScreenHandler.module.setSneakyDirection(SneakyDirection.values()[direction]);
        }
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public @NotNull PacketType<ExtractorModuleDirectionC2SPacket> getType() {
        return TYPE;
    }
}
