package net.danygames2014.logisticspipes.block.pipe.transporter;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.logisticspipes.block.entity.RequestTableLogisticPipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;

public class LogisticItemPipeTransporter extends ItemPipeTransporter {
    private BlockEntity blockEntity;
    public LogisticItemPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        this.blockEntity = blockEntity;
    }

    @Override
    public void dropItem(TravellingItemEntity item) {
        if(blockEntity instanceof RequestTableLogisticPipeBlockEntity) {
            if(item.stack != null && item.stack.count > 0) {
                super.dropItem(item);
            } else {
                item.markDead();
            }
        }
    }
}
