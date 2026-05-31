package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.block.entity.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class BlockEntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event) {
        event.register(NAMESPACE.id("basic_logistic_pipe"), BasicLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("chassis_logistic_pipe"), ChassisLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("supplier_logistic_pipe"), SupplierLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("crafting_logistic_pipe"), CraftingLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("crafting_logistic_pipe_mk2"), CraftingLogisticPipeBlockEntityMk2.class);
        event.register(NAMESPACE.id("satellite_logistic_pipe"), SatelliteLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("provider_logistic_pipe"), ProviderLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("provider_logistic_pipe_mk2"), ProviderLogisticPipeBlockEntityMk2.class);
        event.register(NAMESPACE.id("request_logistic_pipe"), RequestLogisticPipeBlockEntity.class);
        event.register(NAMESPACE.id("request_table"), RequestTableLogisticPipeBlockEntity.class);
    }
}
