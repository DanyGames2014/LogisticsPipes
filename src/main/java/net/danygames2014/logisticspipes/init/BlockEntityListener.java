package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class BlockEntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event){
        event.register(LogisticPipeBlockEntity.class, NAMESPACE.id("logistic_pipe").toString());
    }
}
