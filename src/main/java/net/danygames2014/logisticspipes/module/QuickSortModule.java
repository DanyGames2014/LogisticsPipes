package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.LogisticsManager;
import net.danygames2014.logisticspipes.interfaces.InventoryProvider;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.SendRoutedItem;
import net.danygames2014.logisticspipes.interfaces.WorldProvider;
import net.danygames2014.logisticspipes.util.ParticleColor;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;

public class QuickSortModule implements LogisticsModule {
    private final int ticksToAction = 100;
    private int currentTick = 0;
    private boolean sent;
    private int ticksToResend = 0;

    private InventoryProvider invProvider;
    private SendRoutedItem itemSender;
    private WorldProvider worldProvider;

    private int x;
    private int y;
    private int z;

    public QuickSortModule(){}

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {
        this.invProvider = invProvider;
        this.itemSender = itemSender;
        this.worldProvider = world;
    }

    @Override
    public Identifier getScreenIdentifier() {
        return null;
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        return null;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {
        if(sent) {
            ticksToResend = 6;
            sent = false;
        }

        if(ticksToResend > 0 && --ticksToResend < 1) {
            currentTick = ticksToAction;
        }

        if (++currentTick < ticksToAction) {
            currentTick = 0;
        }

        Inventory targetInventory = invProvider.getInventory();
        if(targetInventory == null) {
            return;
        }
        if(targetInventory.size() < 27) {
            return;
        }
        ItemStack stackToSend;
        for (int i = 0; i < targetInventory.size(); i++) {
            stackToSend = targetInventory.getStack(i);
            if(stackToSend == null) {
                continue;
            }
            if (!this.shouldSend(stackToSend)) {
                continue;
            }
            itemSender.sendStack(stackToSend);
            if(worldProvider.getWorld().getBlockEntity(x, y, z) instanceof LogisticPipeBlockEntity pipe) {
                pipe.queueParticle(ParticleColor.ORANGE, 8);
            }
            targetInventory.setStack(i, null);

            sent = true;
            break;
        }
    }

    protected boolean shouldSend(ItemStack stack) {
        return LogisticsManager.getInstance().hasDestination(worldProvider.getWorld(), stack, true, itemSender.getSourceId(), true);
    }

    @Override
    public void registerPosition(int x, int y, int z, int slot) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {

    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {

    }
}
