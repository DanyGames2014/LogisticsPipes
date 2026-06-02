package net.danygames2014.logisticspipes.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class PacketUtil {
    public static void sendNetworkUpdate(World world, Packet packet, int x, int y, int z, int range) {
        if(world != null && !world.isRemote){
            for(Object o : world.players){
                PlayerEntity player = (PlayerEntity) o;
                if(player.getDistance(x, y, z) < range){
                    PacketHelper.sendTo(player, packet);
                }
            }
        }
    }

    public static void sendToPlayerList(Packet packet, PlayerCollectionList players) {
        for(PlayerEntity player : players.players()) {
            PacketHelper.sendTo(player, packet);
        }
    }
}
