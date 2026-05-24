package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.client.gui.screen.OrderScreen;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.SendNbtCompound;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedList;

public class SendScreenContentS2CPacket extends Packet implements ManagedPacket<SendScreenContentS2CPacket> {
    public static final PacketType<SendScreenContentS2CPacket> TYPE = PacketType.builder(true, false, SendScreenContentS2CPacket::new).build();
    public LinkedList<ItemIdentifierStack> allItems = new LinkedList<>();


    public SendScreenContentS2CPacket() {}

    public SendScreenContentS2CPacket(LinkedList<ItemIdentifierStack> allItems) {
        this.allItems = allItems;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            while (stream.readByte() != 0) { // read until the end
                final int itemID = stream.readInt();
                final int dataValue = stream.readInt();
                final int amount = stream.readInt();
                final NbtCompound tag = SendNbtCompound.readNbtCompound(stream);
                allItems.add(ItemIdentifier.get(Item.ITEMS[itemID], dataValue, tag).makeStack(amount));
            }
        }
        catch (Exception ignored){

        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            for (final ItemIdentifierStack item : allItems) {
                stream.writeByte(1); // byte
                stream.writeInt(item.getItem().item.id);
                stream.writeInt(item.getItem().itemDamage);
                stream.writeInt(item.stackSize);
                SendNbtCompound.writeNbtCompound(item.getItem().nbt, stream);
            }
            stream.writeByte(0);
        }
        catch (Exception ignored){

        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {
            if(Minecraft.INSTANCE.currentScreen instanceof OrderScreen orderScreen){
                orderScreen.handlePacket(this);
            }
        }, null);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<SendScreenContentS2CPacket> getType() {
        return TYPE;
    }
}
