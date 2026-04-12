package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.module.ItemSinkModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

public class ItemSinkScreenHandler extends ModuleScreenHandler {
    private ItemSinkModule module;
    private boolean defaultRoute;
    
    public ItemSinkScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);
        
        if (moduleInventory instanceof ItemSinkModule itemSinkModule) {
            this.module = itemSinkModule;
        }

        addNormalSlotsForPlayerInventory(8, 60);

        // Filter Slots
        for (int pipeSlot = 0; pipeSlot < 9; pipeSlot++) {
            addFilterSlot(pipeSlot, 8 + pipeSlot * 18, 18);
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        if (module != null) {
            listener.onPropertyUpdate(this, 0, this.module.isDefaultRoute() ? 1 : 0);
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.defaultRoute != this.module.isDefaultRoute()) {
                    this.defaultRoute = this.module.isDefaultRoute();
                    listener.onPropertyUpdate(this, 0, this.module.isDefaultRoute() ? 1 : 0);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        if (id == 0) {
            this.module.setDefaultRoute(value == 1);
        }
    }
}
