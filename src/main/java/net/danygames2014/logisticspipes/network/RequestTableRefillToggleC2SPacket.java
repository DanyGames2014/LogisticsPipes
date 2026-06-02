package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.screen.handler.RequestTableScreenHandler;
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

public class RequestTableRefillToggleC2SPacket extends Packet implements ManagedPacket<RequestTableRefillToggleC2SPacket> {
    public static final PacketType<RequestTableRefillToggleC2SPacket> TYPE = PacketType.builder(false, true, RequestTableRefillToggleC2SPacket::new).build();
    
    boolean refillMatrix;

    public RequestTableRefillToggleC2SPacket(boolean refillMatrix) {
        this.refillMatrix = refillMatrix;
    }
    
    public RequestTableRefillToggleC2SPacket() {
        
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            refillMatrix = stream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeBoolean(refillMatrix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (player.currentScreenHandler instanceof RequestTableScreenHandler tableHandler) {
            if (tableHandler.table != null) {
                tableHandler.table.refillMatrix = refillMatrix;    
            }
        }
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public @NotNull PacketType<RequestTableRefillToggleC2SPacket> getType() {
        return TYPE;
    }
}
