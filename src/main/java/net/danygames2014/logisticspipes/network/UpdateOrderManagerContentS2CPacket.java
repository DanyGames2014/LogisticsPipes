package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.interfaces.ChestContentReceiver;
import net.danygames2014.logisticspipes.interfaces.OrderManagerContentReceiver;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class UpdateOrderManagerContentS2CPacket extends InventoryContentPacket implements ManagedPacket<UpdateOrderManagerContentS2CPacket> {

    public static final PacketType<UpdateOrderManagerContentS2CPacket> TYPE = PacketType.builder(true, false, UpdateOrderManagerContentS2CPacket::new).build();

    public UpdateOrderManagerContentS2CPacket() {
    }

    public UpdateOrderManagerContentS2CPacket(int x, int y, int z, LinkedList<ItemIdentifierStack> allItems) {
        super(x, y, z, allItems);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof OrderManagerContentReceiver receiver) {
            receiver.setOrderManagerContent(allItems);
        }
    }

    @Override
    public @NotNull PacketType<UpdateOrderManagerContentS2CPacket> getType() {
        return TYPE;
    }
}
