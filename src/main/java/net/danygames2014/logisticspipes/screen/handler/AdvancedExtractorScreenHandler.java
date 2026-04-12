package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.module.AdvancedExtractorModule;
import net.danygames2014.logisticspipes.util.SneakyDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

public class AdvancedExtractorScreenHandler extends ModuleScreenHandler {
    public AdvancedExtractorModule module;
    private boolean itemsIncluded;
    private int sneakyDirection;
    
    public AdvancedExtractorScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);
        
        if (moduleInventory instanceof AdvancedExtractorModule advancedExtractorModule) {
            this.module = advancedExtractorModule;
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
            listener.onPropertyUpdate(this, 0, this.module.areItemsIncluded() ? 1 : 0);
            listener.onPropertyUpdate(this, 1, this.module.getSneakyDirection().ordinal());
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.itemsIncluded != this.module.areItemsIncluded()) {
                    this.itemsIncluded = this.module.areItemsIncluded();
                    listener.onPropertyUpdate(this, 0, this.module.areItemsIncluded() ? 1 : 0);
                }
                
                if (this.sneakyDirection != this.module.getSneakyDirection().ordinal()) {
                    this.sneakyDirection = this.module.getSneakyDirection().ordinal();
                    listener.onPropertyUpdate(this, 1, this.module.getSneakyDirection().ordinal());
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.module.setItemsIncluded(value == 1);
            }
            
            case 1 -> {
                this.module.setSneakyDirection(SneakyDirection.values()[value]);
            }
        }
    }
}
