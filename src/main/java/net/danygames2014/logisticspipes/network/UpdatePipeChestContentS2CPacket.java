package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.interfaces.ChestContentReceiver;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class UpdatePipeChestContentS2CPacket extends InventoryContentPacket implements ManagedPacket<UpdatePipeChestContentS2CPacket> {

    public static final PacketType<UpdatePipeChestContentS2CPacket> TYPE = PacketType.builder(true, false, UpdatePipeChestContentS2CPacket::new).build();

    public UpdatePipeChestContentS2CPacket() {
    }

    public UpdatePipeChestContentS2CPacket(int x, int y, int z, LinkedList<ItemIdentifierStack> allItems) {
        super(x, y, z, allItems);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof ChestContentReceiver receiver) {
            receiver.setReceivedChestContent(allItems);
        }
    }

    @Override
    public @NotNull PacketType<UpdatePipeChestContentS2CPacket> getType() {
        return TYPE;
    }
}
