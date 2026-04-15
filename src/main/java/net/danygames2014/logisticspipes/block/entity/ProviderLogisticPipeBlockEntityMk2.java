package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.tuple.Pair;

public class ProviderLogisticPipeBlockEntityMk2 extends ProviderLogisticPipeBlockEntity{
    public ProviderLogisticPipeBlockEntityMk2() {
        super();
    }

    public ProviderLogisticPipeBlockEntityMk2(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void tick() {
        super.tick();
        if (!orderManager.hasOrders() || world.getTime() % 6 != 0) return;
        for(int i = 0; i < 16; i++) {
            if(orderManager.hasOrders()) {
                Pair<ItemIdentifierStack, RequestItems> order = orderManager.getNextRequest();
                int sent = sendItem(order.getValue1().getItem(), order.getValue1().stackSize, order.getValue2().getRouter().getRouterId());
                if (sent > 0){
                    orderManager.sendSuccessfull(sent);
                }
                else {
                    orderManager.sendFailed();
                }
//                if(!SimpleServiceLocator.buildCraftProxy.checkMaxItems()) {
//                    break;
//                }
            }
        }
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Fast;
    }
}
