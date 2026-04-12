package net.danygames2014.logisticspipes.network;

import it.unimi.dsi.fastutil.Hash;
import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.danygames2014.logisticspipes.screen.handler.ItemSinkScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.SupplierScreenHandler;
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

public class RequestPartialToggleC2SPacket extends Packet implements ManagedPacket<RequestPartialToggleC2SPacket> {
    public static final PacketType<RequestPartialToggleC2SPacket> TYPE = PacketType.builder(false, true, RequestPartialToggleC2SPacket::new).build();
    
    boolean requestPartial;

    public RequestPartialToggleC2SPacket(boolean requestPartial) {
        this.requestPartial = requestPartial;
    }
    
    public RequestPartialToggleC2SPacket() {
        
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            requestPartial = stream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeBoolean(requestPartial);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (player.currentScreenHandler instanceof SupplierScreenHandler supplierHandler) {
            if (supplierHandler.pipe != null) {
                supplierHandler.pipe.setRequestingPartials(requestPartial);
            }
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NotNull PacketType<RequestPartialToggleC2SPacket> getType() {
        return TYPE;
    }
}
