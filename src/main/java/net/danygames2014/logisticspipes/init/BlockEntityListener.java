package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.block.entity.BasicLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.entity.SupplierLogisticPipeBlockEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class BlockEntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event) {
        event.register(BasicLogisticPipeBlockEntity.class, NAMESPACE.id("basic_logistic_pipe").toString());
        event.register(ChassisLogisticPipeBlockEntity.class, NAMESPACE.id("chassis_logistic_pipe").toString());
        event.register(SupplierLogisticPipeBlockEntity.class, NAMESPACE.id("supplier_logistic_pipe").toString());
    }
}
