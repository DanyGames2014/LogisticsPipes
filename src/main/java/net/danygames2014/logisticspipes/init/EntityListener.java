package net.danygames2014.logisticspipes.init;

import net.danygames2014.buildcraft.entity.*;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class EntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerEntities(EntityRegister event){
        event.register(RoutedItemEntity.class, NAMESPACE.id("routed_item").toString());
    }

    @EventListener
    public void registerEntityHandlers(EntityHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("routed_item"), RoutedItemEntity::new);
    }
}
