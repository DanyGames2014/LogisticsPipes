package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.RequestItems;

public class RemoteOrdererLogisticPipeBlockEntity extends LogisticPipeBlockEntity implements RequestItems {
    public RemoteOrdererLogisticPipeBlockEntity() {
        super();
    }

    public RemoteOrdererLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return null;
    }

    @Override
    public void setup() {

    }

    @Override
    public ItemSendMode getItemSendMode() {
        return null;
    }


    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }
}
