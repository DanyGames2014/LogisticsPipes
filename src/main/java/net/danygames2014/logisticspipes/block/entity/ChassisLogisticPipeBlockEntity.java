package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.module.ItemSinkModule;

public class ChassisLogisticPipeBlockEntity extends LogisticPipeBlockEntity {
    private ItemSinkModule itemSinkModule;

    public ChassisLogisticPipeBlockEntity() {
        super();
    }

    public ChassisLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return null;
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return itemSinkModule;
    }

    @Override
    public void setup() {
        itemSinkModule = new ItemSinkModule();
    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }
}
