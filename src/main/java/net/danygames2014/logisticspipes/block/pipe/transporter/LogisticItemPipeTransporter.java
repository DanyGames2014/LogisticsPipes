package net.danygames2014.logisticspipes.block.pipe.transporter;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.logisticspipes.block.entity.RequestTableLogisticPipeBlockEntity;

public class LogisticItemPipeTransporter extends ItemPipeTransporter {
    public LogisticItemPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void dropItem(TravellingItemEntity item) {
        if (blockEntity instanceof RequestTableLogisticPipeBlockEntity) {
            if (item.stack != null && item.stack.count > 0) {
                super.dropItem(item);
            } else {
                item.markDead();
            }
            return;
        }
        
        super.dropItem(item);
    }
}
