package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.danygames2014.logisticspipes.screen.handler.ItemSinkScreenHandler;
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

public class DefaultRouteToggleC2SPacket extends Packet implements ManagedPacket<DefaultRouteToggleC2SPacket> {
    public static final PacketType<DefaultRouteToggleC2SPacket> TYPE = PacketType.builder(false, true, DefaultRouteToggleC2SPacket::new).build();
    
    boolean defaultRoute;

    public DefaultRouteToggleC2SPacket(boolean defaultRoute) {
        this.defaultRoute = defaultRoute;
    }
    
    public DefaultRouteToggleC2SPacket() {
        
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.defaultRoute = stream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeBoolean(defaultRoute);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        
        if (player.currentScreenHandler instanceof ItemSinkScreenHandler itemSinkHandler) {
            if (itemSinkHandler.moduleInventory instanceof ItemSinkModule itemSinkModule) {
                itemSinkModule.setDefaultRoute(defaultRoute);
            }
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NotNull PacketType<DefaultRouteToggleC2SPacket> getType() {
        return TYPE;
    }
}
