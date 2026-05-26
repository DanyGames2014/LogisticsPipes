package net.danygames2014.logisticspipes.init;

import net.danygames2014.logisticspipes.block.entity.RequestLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.client.render.block.entity.InvisibleBlockEntityRenderer;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;

public class BlockEntityRendererListener {
    @EventListener
    public void registerBlockEntityRenderers(BlockEntityRendererRegisterEvent event){
        event.renderers.put(RequestLogisticPipeBlockEntity.class, InvisibleBlockEntityRenderer.INSTANCE);
    }
}
