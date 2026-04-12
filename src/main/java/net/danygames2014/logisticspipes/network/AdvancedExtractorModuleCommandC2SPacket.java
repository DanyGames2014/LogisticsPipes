package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.module.AdvancedExtractorModule;
import net.danygames2014.logisticspipes.module.ProviderModule;
import net.danygames2014.logisticspipes.screen.handler.AdvancedExtractorScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.ProviderScreenHandler;
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

public class AdvancedExtractorModuleCommandC2SPacket extends Packet implements ManagedPacket<AdvancedExtractorModuleCommandC2SPacket> {
    public static final PacketType<AdvancedExtractorModuleCommandC2SPacket> TYPE = PacketType.builder(false, true, AdvancedExtractorModuleCommandC2SPacket::new).build();
    
    int commandId;
    int commandValue;

    public AdvancedExtractorModuleCommandC2SPacket(int commandId, int commandValue) {
        this.commandId = commandId;
        this.commandValue = commandValue;
    }
    
    public AdvancedExtractorModuleCommandC2SPacket() {
        
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

        if (player.currentScreenHandler instanceof AdvancedExtractorScreenHandler advancedExtractorScreenHandler) {
            if (advancedExtractorScreenHandler.moduleInventory instanceof AdvancedExtractorModule advancedExtractorModule) {
                switch (commandId) {
                    case 0 -> {
                        advancedExtractorModule.setItemsIncluded(commandValue == 1);
                    }
                    
                    case 1 -> {
                        if (commandValue < 0 || commandValue > 3) {
                            return;
                        }
                        
                        advancedExtractorModule.setSneakyDirection(SneakyDirection.values()[commandValue]);
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
    public @NotNull PacketType<AdvancedExtractorModuleCommandC2SPacket> getType() {
        return TYPE;
    }
}
