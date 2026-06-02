package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.interfaces.RoutedItem;
import net.danygames2014.logisticspipes.screen.handler.RequestTableScreenHandler;
import net.danygames2014.logisticspipes.util.ItemUtil;
import net.danygames2014.logisticspipes.util.SimpleInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.math.Direction;

public class RequestTableLogisticPipeBlockEntity extends RequestLogisticPipeBlockEntity {

    public SimpleInventory inv = new SimpleInventory(27, "Crafting Resources", 64, this::markInventoryDirty);
    public SimpleInventory matrix = new SimpleInventory(9, "Crafting Matrix", 64, this::markInventoryDirty);
    public SimpleInventory toSortInv = new SimpleInventory(1, "Sorting Slot", 64, this::markInventoryDirty);

    public boolean refillMatrix = true;
    private int delay = 0;

    public RequestTableLogisticPipeBlockEntity() {
    }

    public RequestTableLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public void markInventoryDirty() {
        super.markInventoryDirty();
    }

    @Override
    public void openModuleScreen(PlayerEntity player) {
        GuiHelper.openGUI(player, LogisticsPipes.NAMESPACE.id("request_table"), inv, new RequestTableScreenHandler(player, this), (messagePacket) -> {
            messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
        });
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inv.writeNbt(nbt, "inv");
        matrix.writeNbt(nbt, "matrix");
        toSortInv.writeNbt(nbt, "toSortInv");
        nbt.putBoolean("refillMatrix", refillMatrix);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inv.readNbt(nbt, "inv");
        matrix.readNbt(nbt, "matrix");
        toSortInv.readNbt(nbt, "toSortInv");
        refillMatrix = nbt.getBoolean("refillMatrix");
    }

    @Override
    public void tick() {
        super.tick();
        ItemStack stack = toSortInv.getStack(0);
        if(stack != null) {
            if(delay > 0) {
                delay--;
                return;
            }
            RoutedItem itemToSend = ItemUtil.createRoutedItem(stack, this.world);
            LogisticsManager.getInstance().assignDestinationFor(this.world, itemToSend, this.getRouter().getRouterId(), false);
            if(itemToSend.isDestinationValid()) {
                Direction dir = getDirectionForItem(itemToSend);
                super.queueRoutedItem(itemToSend, dir.getOpposite());
                toSortInv.setStack(0, null);
            } else {
                delay = 100;
            }
        } else {
            delay = 0;
        }
    }

    // TODO: check if this is correct
    @Override
    public Direction itemArrived(RoutedItem item) {
        if(item.getItemStack() != null) {
            statLifetimeReceived++;
            statSessionReceived++;
            ItemStack stack = item.getItemStack();
            stack.count = inv.addCompressed(stack);
            item.setItemStack(stack);
            if(stack.count > 0){
                return super.itemArrived(item);
            }
        }
        return null;
    }
}
