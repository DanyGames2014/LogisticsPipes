package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetDiskNameC2SPacket extends CoordinatesPacket implements ManagedPacket<SetDiskNameC2SPacket> {

    public String name;

    public static final PacketType<SetDiskNameC2SPacket> TYPE = PacketType.builder(false, true, SetDiskNameC2SPacket::new).build();

    public SetDiskNameC2SPacket() {
        super();
    }

    public SetDiskNameC2SPacket(int x, int y, int z, String name) {
        super(x, y, z);
        this.name = name;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            name = stream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeUTF(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        super.apply(networkHandler);
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof RequestLogisticPipeBlockEntity pipe) {
            ItemStack stack = pipe.getDiskInventory().getStack(0);
            if(stack != null){
                stack.getStationNbt().putString("name", name);
            }
        }
    }

    @Override
    public @NotNull PacketType<SetDiskNameC2SPacket> getType() {
        return TYPE;
    }
}
