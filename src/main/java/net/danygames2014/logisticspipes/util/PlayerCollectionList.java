package net.danygames2014.logisticspipes.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.modificationstation.stationapi.api.util.SideUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerCollectionList {
    private final List<EqualWeakReference<PlayerEntity>> players = new ArrayList<>();
    private  boolean checkingPlayers = false;

    public void checkPlayers() {
        checkingPlayers = true;
        Iterator<EqualWeakReference<PlayerEntity>> iPlayers = players.iterator();
        while(iPlayers.hasNext()) {
            EqualWeakReference<PlayerEntity> playerReference = iPlayers.next();
            boolean remove = false;
            if(playerReference.get() == null) {
                remove = true;
            } else if (playerReference.get() != null && playerReference.get().dead) {
                remove = true;
            }
            if(!remove) {
                remove = SideUtil.get(() -> false, () -> {
                    if(playerReference.get() instanceof ServerPlayerEntity serverPlayer ){
                        return serverPlayer.networkHandler.disconnected;
                    }
                    return false;
                });
            }
            if(remove) {
                iPlayers.remove();
            }
        }
        checkingPlayers = false;
    }

    public Iterable<PlayerEntity> players() {
        checkPlayers();
        return () -> new Itr(players.iterator());
    }

    public int size() {
        if(!checkingPlayers)
            checkPlayers();
        return players.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void add(PlayerEntity player) {
        players.add(new EqualWeakReference<>(player));
    }

    public boolean remove(PlayerEntity player) {
        return players.remove(new EqualWeakReference<>(player));
    }

    public boolean contains(PlayerEntity player) {
        checkPlayers();
        return players.contains(new EqualWeakReference<>(player));
    }

    private record Itr(Iterator<EqualWeakReference<PlayerEntity>> iterator) implements Iterator<PlayerEntity> {

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public PlayerEntity next() {
            EqualWeakReference<PlayerEntity> reference = iterator.next();
            return reference.get();
        }

        public void remove() {
            iterator.remove();
        }
    }
}
