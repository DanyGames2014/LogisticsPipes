package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.CraftingLogisticPipeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

public class CraftingPipeScreenHandler extends ModuleScreenHandler {
    public final CraftingLogisticPipeBlockEntity pipe;
    
    private int satelliteId = 0;
    private int priority = 0;
    
    public CraftingPipeScreenHandler(PlayerEntity player, CraftingLogisticPipeBlockEntity pipe) {
        super(player, pipe.getDummyInventory());
        this.pipe = pipe;

        addNormalSlotsForPlayerInventory(18, 97);

        for(int l = 0; l < 9; l++) {
            addFilterSlot(l, 18 + l * 18, 18);
        }

        addFilterSlot(9, 90, 64);
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.pipe.satelliteId);
        listener.onPropertyUpdate(this, 1, this.pipe.priority);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.satelliteId != this.pipe.satelliteId) {
                    this.satelliteId = this.pipe.satelliteId;
                    listener.onPropertyUpdate(this, 0, this.pipe.satelliteId);
                }

                if (this.priority != this.pipe.priority) {
                    this.priority = this.pipe.priority;
                    listener.onPropertyUpdate(this, 1, this.pipe.priority);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.pipe.satelliteId = value;
            }
            
            case 1 -> {
                this.pipe.priority = value;
            }
        }
    }
}
