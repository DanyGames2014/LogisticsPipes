package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestDiskMacroC2SPacket extends CoordinatesPacket implements ManagedPacket<RequestDiskMacroC2SPacket> {

    public int macroIndex = -1;

    public static final PacketType<RequestDiskMacroC2SPacket> TYPE = PacketType.builder(false, true, RequestDiskMacroC2SPacket::new).build();

    public RequestDiskMacroC2SPacket() {
        super();
    }

    public RequestDiskMacroC2SPacket(int x, int y, int z, int macroIndex) {
        super(x, y, z);
        this.macroIndex = macroIndex;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            this.macroIndex = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(this.macroIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        super.apply(networkHandler);
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof RequestLogisticPipeBlockEntity pipe) {
            ItemStack disk = pipe.getDiskInventory().getStack(0);
            if(disk == null) {
                return;
            }

            if(!disk.getStationNbt().contains("macroList")) {
                disk.getStationNbt().put("macroList", new NbtList());
            }

            NbtList list = disk.getStationNbt().getList("macroList");
            for(int i = 0;i < list.size();i++) {
                if(i == macroIndex) {
                    NbtCompound itemlist = (NbtCompound) list.get(i);
                    RequestHandler.requestMacrolist(itemlist, pipe, player);
                    break;
                }
            }
            boolean flag = false;
        }
    }

    @Override
    public @NotNull PacketType<RequestDiskMacroC2SPacket> getType() {
        return TYPE;
    }
}
