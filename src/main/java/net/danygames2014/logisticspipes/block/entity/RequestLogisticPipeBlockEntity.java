package net.danygames2014.logisticspipes.block.entity;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.RequestMk2LogisticPipeBlock;
import net.danygames2014.logisticspipes.block.pipe.ItemSendMode;
import net.danygames2014.logisticspipes.entity.RoutedItemEntity;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.danygames2014.logisticspipes.screen.handler.SupplierScreenHandler;
import net.danygames2014.logisticspipes.util.SimpleInventory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;

public class RequestLogisticPipeBlockEntity extends LogisticPipeBlockEntity {

    private SimpleInventory diskInventory = new SimpleInventory(1, "Disk", 1, this::markDirty);

    public RequestLogisticPipeBlockEntity() {
        super();
    }

    public RequestLogisticPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Normal;
    }

    @Override
    public LogisticsModule getLogisticsModule() {
        return null;
    }

    @Override
    public void setup() {
    }

    @Override
    public LogisticPipeBlockEntity getPipe() {
        return this;
    }

    @Override
    public void itemDropped(RoutedItemEntity routedItemEntity) {

    }

    public void dropDisk() {
        ItemStack disk = getDiskInventory().getStack(0);
        if(disk != null) {
            ItemEntity item = new ItemEntity(world, x, y, z, disk);
            world.spawnEntity(item);
            getDiskInventory().setStack(0, null);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        diskInventory.writeNbt(nbt, "");
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        diskInventory.readNbt(nbt, "");
    }

    public void openModuleScreen(PlayerEntity player) {
        boolean isMk2 = world.getBlockState(x, y, z).getBlock() == LogisticsPipes.requestItemPipeMk2;
        GuiHelper.openGUI(player, isMk2 ? LogisticsPipes.NAMESPACE.id("normal_order_mk2") : LogisticsPipes.NAMESPACE.id("normal_order"), diskInventory, new ModuleScreenHandler(player, diskInventory), (messagePacket) -> {
            messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
        });
    }

    public SimpleInventory getDiskInventory(){
        return diskInventory;
    }

    public void markDirty() {

    }
}
