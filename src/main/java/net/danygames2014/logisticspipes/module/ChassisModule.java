package net.danygames2014.logisticspipes.module;

import net.danygames2014.logisticspipes.LogisticsPipes;
import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.InventoryProvider;
import net.danygames2014.logisticspipes.interfaces.LogisticsModule;
import net.danygames2014.logisticspipes.interfaces.SendRoutedItem;
import net.danygames2014.logisticspipes.interfaces.WorldProvider;
import net.danygames2014.logisticspipes.screen.handler.ChassisScreenHandler;
import net.danygames2014.logisticspipes.util.InventoryUtil;
import net.danygames2014.logisticspipes.util.ItemIdentifier;
import net.danygames2014.logisticspipes.util.SinkReply;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.util.Identifier;

public class ChassisModule implements LogisticsModule {
    private final LogisticsModule[] modules;
    private final ChassisLogisticPipeBlockEntity parentPipe;

    public ChassisModule(int moduleCount, ChassisLogisticPipeBlockEntity parentPipe) {
        this.modules = new LogisticsModule[moduleCount];
        this.parentPipe = parentPipe;
    }

    public void installModule(int slot, LogisticsModule module) {
        modules[slot] = module;
    }

    public void removeModule(int slot) {
        modules[slot] = null;
    }

    public LogisticsModule getModule(int slot) {
        return modules[slot];
    }

    public boolean hasModule(int slot) {
        return (modules[slot] != null);
    }

    @Override
    public SinkReply sinksItem(ItemStack item) {
        //Always deny items when we can't put the item anywhere
        Inventory inv = parentPipe.getInventory();
        if (inv == null) return null;
        InventoryUtil invUtil = new InventoryUtil(inv, false);
        int roomForItem = invUtil.roomForItem(ItemIdentifier.get(item));

        if (roomForItem < 1) return null;

        for (LogisticsModule module : modules) {
            if (module != null) {
                SinkReply result = module.sinksItem(item);
                if (result != null) {
                    result.maxNumberOfItems = roomForItem;
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        if (slot < 0 || slot >= modules.length) return null;
        return modules[slot];
    }

    @Override
    public void readNbt(NbtCompound nbt, String prefix) {
        for (int i = 0; i < modules.length; i++) {
            if (modules[i] != null) {
                NbtCompound slot = nbt.getCompound("slot" + i);
                if (slot != null) {
                    modules[i].readNbt(slot, "");
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, String prefix) {
        for (int i = 0; i < modules.length; i++) {
            if (modules[i] != null) {
                NbtCompound slot = new NbtCompound();
                modules[i].writeNbt(slot, "");
                nbt.put("slot" + i, slot);
            }
        }
    }

    @Override
    public void tick() {
        for (LogisticsModule module : modules) {
            if (module == null) continue;
            module.tick();
        }
    }

    @Override
    public void registerHandler(InventoryProvider invProvider, SendRoutedItem itemSender, WorldProvider world) {

    }

    @Override
    public void registerPosition(int x, int y, int z, int slot) {

    }

    @Override
    public Identifier getScreenIdentifier() {
        return LogisticsPipes.NAMESPACE.id("chassis");
    }

    @Override
    public ScreenHandler getScreenHandler(PlayerEntity player) {
        return new ChassisScreenHandler(player, parentPipe);
    }
}
