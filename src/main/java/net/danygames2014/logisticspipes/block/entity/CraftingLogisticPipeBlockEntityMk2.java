package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.interfaces.RequestItems;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.util.AdjacentBlockEntity;
import net.danygames2014.logisticspipes.util.ItemIdentifierStack;
import net.danygames2014.logisticspipes.util.ItemUtil;
import net.danygames2014.logisticspipes.util.NyalibInventoryUtil;
import net.danygames2014.logisticspipes.util.tuple.Pair;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;

public class CraftingLogisticPipeBlockEntityMk2 extends CraftingLogisticPipeBlockEntity {

    public CraftingLogisticPipeBlockEntityMk2() {
        super();
    }

    public CraftingLogisticPipeBlockEntityMk2(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void tick() {
        super.tick();
        if ((!orderManager.hasOrders() && extras < 1) || world.getTime() % 6 != 0) return;

        LinkedList<AdjacentBlockEntity> crafters = locateCrafters();
        if (crafters.isEmpty()) {
            orderManager.sendFailed();
            return;
        }

        for(int i = 0; i < 16; i++) {
            if ((!orderManager.hasOrders() && extras < 1)) break;
            for (AdjacentBlockEntity adjacent : locateCrafters()) {
                ItemStack extracted = null;
                if (NyalibInventoryUtil.hasItemHandler(adjacent.blockEntity)) {
                    extracted = extractFromInventory(NyalibInventoryUtil.getWrappedItemHandler(adjacent.blockEntity, adjacent.direction.getOpposite()));
                }
                if (extracted == null) break;
                while (extracted.count > 0) {
                    ItemStack stackToSend = extracted.split(1);
                    Position p = new Position(adjacent.blockEntity.x, adjacent.blockEntity.y, adjacent.blockEntity.z, adjacent.direction);
                    if (orderManager.hasOrders()) {
                        Pair<ItemIdentifierStack, RequestItems> order = orderManager.getNextRequest();
                        RoutedItem item = ItemUtil.createRoutedItem(stackToSend, world);
                        item.setSource(this.getRouter().getRouterId());
                        item.setDestination(order.getValue2().getRouter().getRouterId());
                        item.setTransportMode(RoutedItem.TransportMode.Active);
                        super.queueRoutedItem(item, adjacent.direction);
                        orderManager.sendSuccessfull(1);
                    } else {
                        extras--;
//                        if(LogisticsPipes.DisplayRequests)System.out.println("Extra dropped, " + _extras + " remaining");
//                        Position entityPos = new Position(p.x + 0.5, p.y + Utils.getPipeFloorOf(stackToSend), p.z + 0.5, p.orientation.reverse());
//                        entityPos.moveForwards(0.5);
//                        EntityPassiveItem entityItem = new EntityPassiveItem(worldObj, entityPos.x, entityPos.y, entityPos.z, stackToSend);
//                        entityItem.setSpeed(Utils.pipeNormalSpeed * Configs.LOGISTICS_DEFAULTROUTED_SPEED_MULTIPLIER);
//                        ((PipeTransportItems) transport).entityEntering(entityItem, entityPos.orientation);
                    }
                }
            }

            // TODO: maybe we have to check the buildcraft limit here but in the original it just checks if the max items are >= 1000 globally
            if ((!orderManager.hasOrders() && extras < 1)) { // || !SimpleServiceLocator.buildCraftProxy.checkMaxItems()
                break;
            }
        }
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Fast;
    }
}
