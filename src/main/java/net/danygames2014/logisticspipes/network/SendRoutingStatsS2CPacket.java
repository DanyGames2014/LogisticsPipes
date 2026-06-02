package net.danygames2014.logisticspipes.network;

import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.client.gui.screen.OrderScreen;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.SendNbtCompound;
import net.danygames2014.nyalib.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class SendRoutingStatsS2CPacket extends CoordinatesPacket implements ManagedPacket<SendRoutingStatsS2CPacket> {
    public static final PacketType<SendRoutingStatsS2CPacket> TYPE = PacketType.builder(true, false, SendRoutingStatsS2CPacket::new).build();

    public int statSessionSent;
    public int statSessionReceived;
    public int statSessionRelayed;

    public long statLifetimeSent;
    public long statLifetimeReceived;
    public long statLifetimeRelayed;

    public int networkSize;

    public SendRoutingStatsS2CPacket() {
    }

    public SendRoutingStatsS2CPacket(int x, int y, int z, int statSessionSent, int statSessionReceived, int statSessionRelayed, long statLifetimeSent, long statLifetimeReceived, long statLifetimeRelayed, int networkSize) {
        super(x, y, z);
        this.statSessionSent = statSessionSent;
        this.statSessionReceived = statSessionReceived;
        this.statSessionRelayed = statSessionRelayed;
        this.statLifetimeSent = statLifetimeSent;
        this.statLifetimeReceived = statLifetimeReceived;
        this.statLifetimeRelayed = statLifetimeRelayed;
        this.networkSize = networkSize;
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            this.statSessionSent = stream.readInt();
            this.statSessionReceived = stream.readInt();
            this.statSessionRelayed = stream.readInt();
            this.statLifetimeSent = stream.readLong();
            this.statLifetimeReceived = stream.readLong();
            this.statLifetimeRelayed = stream.readLong();
            this.networkSize = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);
        try {
            stream.writeInt(statSessionSent);
            stream.writeInt(statSessionReceived);
            stream.writeInt(statSessionRelayed);
            stream.writeLong(statLifetimeSent);
            stream.writeLong(statLifetimeReceived);
            stream.writeLong(statLifetimeRelayed);
            stream.writeInt(networkSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
            pipe.statSessionSent = statSessionSent;
            pipe.statSessionReceived = statSessionReceived;
            pipe.statSessionRelayed = statSessionRelayed;
            pipe.statLifetimeSent = statLifetimeSent;
            pipe.statLifetimeReceived = statLifetimeReceived;
            pipe.statLifetimeRelayed = statLifetimeRelayed;
            pipe.networkSize = networkSize;
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<SendRoutingStatsS2CPacket> getType() {
        return TYPE;
    }
}
