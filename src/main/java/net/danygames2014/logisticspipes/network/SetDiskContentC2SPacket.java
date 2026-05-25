package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SetDiskContentC2SPacket extends ItemPacket implements ManagedPacket<SetDiskContentC2SPacket> {
    public static final PacketType<SetDiskContentC2SPacket> TYPE = PacketType.builder(false, true, SetDiskContentC2SPacket::new).build();

    public SetDiskContentC2SPacket() {
    }

    public SetDiskContentC2SPacket(int x, int y, int z, ItemStack stack) {
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
            PacketHelper.sendTo(player, new SendDiskContentS2CPacket(x, y, z, pipe.getDiskInventory().getStack(0)));
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
