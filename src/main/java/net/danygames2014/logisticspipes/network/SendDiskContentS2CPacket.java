package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SendDiskContentS2CPacket extends ItemPacket implements ManagedPacket<SendDiskContentS2CPacket> {
    public static final PacketType<SendDiskContentS2CPacket> TYPE = PacketType.builder(true, false, SendDiskContentS2CPacket::new).build();

    public SendDiskContentS2CPacket() {
    }

    public SendDiskContentS2CPacket(int x, int y, int z, ItemStack stack) {
        super(x, y, z, stack);
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof RequestLogisticPipeBlockEntity pipe){
            pipe.getDiskInventory().setStack(0, stack);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType getType() {
        return TYPE;
    }
}
