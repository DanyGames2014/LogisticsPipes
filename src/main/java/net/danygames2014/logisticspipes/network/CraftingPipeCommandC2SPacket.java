package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.CraftingLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.module.AdvancedExtractorModule;
import net.danygames2014.logisticspipes.screen.handler.AdvancedExtractorScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.CraftingPipeScreenHandler;
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

public class CraftingPipeCommandC2SPacket extends Packet implements ManagedPacket<CraftingPipeCommandC2SPacket> {
    public static final PacketType<CraftingPipeCommandC2SPacket> TYPE = PacketType.builder(false, true, CraftingPipeCommandC2SPacket::new).build();
    
    private int commandId;

    public CraftingPipeCommandC2SPacket(int commandId) {
        this.commandId = commandId;
    }

    public CraftingPipeCommandC2SPacket() {
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.commandId = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(commandId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (player.currentScreenHandler instanceof CraftingPipeScreenHandler craftingPipeScreenHandler) {
            CraftingLogisticPipeBlockEntity pipe = craftingPipeScreenHandler.pipe;
            
            switch (commandId) {
                case 0 -> {
                    pipe.setNextSatellite(player);
                }

                case 1 -> {
                    pipe.setPrevSatellite(player);
                }
                
                case 2 -> {
                    // Paint Path to Satellite
                }
                
                case 3 -> {
                    pipe.importFromCraftingTable(player);
                }
                
                case 4 -> {
                    pipe.openAttachedGui(player);
                }
                
                case 20 -> {
                    pipe.priorityUp(player);
                }
                
                case 21 -> {
                    pipe.priorityDown(player);
                }
            }
        }
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public @NotNull PacketType<CraftingPipeCommandC2SPacket> getType() {
        return TYPE;
    }
}
