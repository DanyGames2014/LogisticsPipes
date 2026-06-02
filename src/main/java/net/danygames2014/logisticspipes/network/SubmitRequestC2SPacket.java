package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.request.RequestHandler;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SendNbtCompound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SubmitRequestC2SPacket extends CoordinatesPacket implements ManagedPacket<SubmitRequestC2SPacket> {
    public int itemID;
    public int dataValue;
    public int amount;
    public NbtCompound tag;

    public static final PacketType<SubmitRequestC2SPacket> TYPE = PacketType.builder(false, true, SubmitRequestC2SPacket::new).build();

    public SubmitRequestC2SPacket() {
        super();
    }

    public SubmitRequestC2SPacket(int x, int y, int z, ItemIdentifier selectedItem, int amount) {
        super(x, y, z);
        itemID = selectedItem.item.id;
        dataValue = selectedItem.itemDamage;
        tag = selectedItem.nbt;
        this.amount = amount;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            itemID = stream.readInt();
            dataValue = stream.readInt();
            amount = stream.readInt();
            tag = SendNbtCompound.readNbtCompound(stream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(itemID);
            stream.writeInt(dataValue);
            stream.writeInt(amount);
            SendNbtCompound.writeNbtCompound(tag, stream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
            RequestHandler.request(player, this, pipe);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<SubmitRequestC2SPacket> getType() {
        return TYPE;
    }
}
