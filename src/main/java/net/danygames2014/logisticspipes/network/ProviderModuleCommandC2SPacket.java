package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.module.ProviderModule;
import net.danygames2014.logisticspipes.screen.handler.ProviderScreenHandler;
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

public class ProviderModuleCommandC2SPacket extends Packet implements ManagedPacket<ProviderModuleCommandC2SPacket> {
    public static final PacketType<ProviderModuleCommandC2SPacket> TYPE = PacketType.builder(false, true, ProviderModuleCommandC2SPacket::new).build();
    
    int commandId;
    int commandValue;

    public ProviderModuleCommandC2SPacket(int commandId, int commandValue) {
        this.commandId = commandId;
        this.commandValue = commandValue;
    }
    
    public ProviderModuleCommandC2SPacket() {
        
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            commandId = stream.readInt();
            commandValue = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(commandId);
            stream.writeInt(commandValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (player.currentScreenHandler instanceof ProviderScreenHandler providerHandler) {
            if (providerHandler.moduleInventory instanceof ProviderModule providerModule) {
                switch (commandId) {
                    case 0 -> {
                        providerModule.setFilterExcluded(commandValue == 1);
                    }
                    
                    case 1 -> {
                        if (commandValue < 0 || commandValue > 4) {
                            return;
                        }
                        
                        providerModule.setExtractionMode(ExtractionMode.values()[commandValue]);
                    }
                }
            }
        }
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public @NotNull PacketType<ProviderModuleCommandC2SPacket> getType() {
        return TYPE;
    }
}
