package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.module.ProviderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

public class ProviderScreenHandler extends ModuleScreenHandler {
    private ProviderModule module;
    private boolean isExcludeFilter;
    private int extractionMode;
    
    public ProviderScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);
        
        if (moduleInventory instanceof ProviderModule providerModule) {
            this.module = providerModule;
        }

        addNormalSlotsForPlayerInventory(18, 97);

        int xOffset = 72;
        int yOffset = 18;

        // Filter Slots
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addFilterSlot(column + row * 3, xOffset + column * 18, yOffset + row * 18);
            }
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        if (module != null) {
            listener.onPropertyUpdate(this, 0, this.module.isExcludeFilter() ? 1 : 0);
            listener.onPropertyUpdate(this, 1, this.module.getExtractionMode().ordinal());
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.isExcludeFilter != this.module.isExcludeFilter()) {
                    this.isExcludeFilter = this.module.isExcludeFilter();
                    listener.onPropertyUpdate(this, 0, this.module.isExcludeFilter() ? 1 : 0);
                }

                if (this.extractionMode != this.module.getExtractionMode().ordinal()) {
                    this.extractionMode = this.module.getExtractionMode().ordinal();
                    listener.onPropertyUpdate(this, 1, this.module.getExtractionMode().ordinal());
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.module.setFilterExcluded(value == 1);    
            }
            
            case 1 -> {
                this.module.setExtractionMode(ExtractionMode.values()[value]);
            }
        }
    }
}
