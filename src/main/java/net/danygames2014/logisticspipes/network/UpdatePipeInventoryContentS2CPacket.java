package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ChestContentReceiver;
import net.danygames2014.logisticspipes.interfaces.ModuleInventoryReceive;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class UpdatePipeInventoryContentS2CPacket extends InventoryContentPacket implements ManagedPacket<UpdatePipeInventoryContentS2CPacket> {

    public static final PacketType<UpdatePipeInventoryContentS2CPacket> TYPE = PacketType.builder(true, false, UpdatePipeInventoryContentS2CPacket::new).build();

    public UpdatePipeInventoryContentS2CPacket() {
    }

    public UpdatePipeInventoryContentS2CPacket(int x, int y, int z, LinkedList<ItemIdentifierStack> allItems) {
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
    public @NotNull PacketType<UpdatePipeInventoryContentS2CPacket> getType() {
        return TYPE;
    }
}
