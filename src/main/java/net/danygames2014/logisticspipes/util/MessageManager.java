package net.danygames2014.logisticspipes.util;

import net.danygames2014.logisticspipes.network.ItemMessageS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.util.LinkedList;

public class MessageManager {
    public static void overflow(PlayerEntity player, ItemIdentifier item) {
        LinkedList<ItemMessage> error = new LinkedList<ItemMessage>();
        error.add(new ItemMessage(item.item, item.itemDamage, 1, item.nbt));
        PacketHelper.sendTo(player, new ItemMessageS2CPacket(error));
    }

    public static void errors(PlayerEntity player, LinkedList<ItemMessage> errors) {
        PacketHelper.sendTo(player, new ItemMessageS2CPacket(errors,true));
    }

    public static void requested(PlayerEntity player, LinkedList<ItemMessage> items) {
        PacketHelper.sendTo(player, new ItemMessageS2CPacket(items,false));
    }
}
