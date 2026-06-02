package net.danygames2014.logisticspipes.network;

import net.danygames2014.logisticspipes.client.gui.screen.OrderScreen;
import net.danygames2014.logisticspipes.gui.SubScreenController;
import net.danygames2014.logisticspipes.util.ItemMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import java.util.List;

public class ItemMessageS2CPacket extends Packet implements ManagedPacket<ItemMessageS2CPacket> {

    public List<ItemMessage> items = new LinkedList<>();
    public boolean error = true;

    public ItemMessageS2CPacket(){
    }

    public ItemMessageS2CPacket(List<ItemMessage> errors){
        this.items = errors;
    }

    public ItemMessageS2CPacket(List<ItemMessage> items, boolean flag){
        this(items);
        this.error = flag;
    }

    public static final PacketType<ItemMessageS2CPacket> TYPE = PacketType.builder(true, false, ItemMessageS2CPacket::new).build();

    @Override
    public void read(DataInputStream stream) {
        try {
            this.error = stream.readBoolean();
            while(stream.readByte() != 0) {
                ItemMessage error = new ItemMessage();
                error.item = Item.ITEMS[stream.readInt()];
                error.data = stream.readInt();
                error.amount = stream.readInt();
                items.add(error);
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeBoolean(error);
            for(ItemMessage error:items) {
                stream.writeByte(1);
                stream.writeInt(error.item.id);
                stream.writeInt(error.data);
                stream.writeInt(error.amount);
            }
            stream.writeByte(0);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        SideUtil.run(() -> {
            if(Minecraft.INSTANCE.currentScreen instanceof OrderScreen screen) {
                screen.handleRequestAnswer(items, !error, screen, player);
            }
            else if(error) {
                for(ItemMessage item : items) {
                    player.sendMessage("Missing: " + item);
                }
            } else {
                for(ItemMessage item : items) {
                    player.sendMessage("Requested: " + item);
                }
                player.sendMessage("Request successful!");
            }
        }, null);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<ItemMessageS2CPacket> getType() {
        return TYPE;
    }
}
