package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.screen.handler.ChassisScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OpenModuleScreenC2SPacket extends Packet implements ManagedPacket<OpenModuleScreenC2SPacket> {
    public static final PacketType<OpenModuleScreenC2SPacket> TYPE = PacketType.builder(false, true, OpenModuleScreenC2SPacket::new).build();
    
    int moduleSlot;

    public OpenModuleScreenC2SPacket(int moduleSlot) {
        this.moduleSlot = moduleSlot;
    }
    
    public OpenModuleScreenC2SPacket() {
        
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            moduleSlot = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(moduleSlot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        if (moduleSlot >= 0 && moduleSlot <= 7) {
            if (player.currentScreenHandler instanceof ChassisScreenHandler handler) {
                ChassisLogisticPipeBlockEntity pipe = handler.pipe;
                LogisticsModule chassisModule = pipe.getLogisticsModule();
                if (chassisModule == null) {
                    return;
                }
                
                LogisticsModule subModule = chassisModule.getSubModule(moduleSlot);
                if (subModule == null) {
                    return;
                }

                GuiHelper.openGUI(player, subModule.getScreenIdentifier(), pipe, subModule.getScreenHandler(player), (messagePacket) -> {
                    messagePacket.ints = new int[]{messagePacket.ints[0], pipe.getX(), pipe.getY(), pipe.getZ(), moduleSlot};
                });
            }
        }
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public @NotNull PacketType<OpenModuleScreenC2SPacket> getType() {
        return TYPE;
    }
}
