package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.ProviderLogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.block.pipe.ExtractionMode;
import net.danygames2014.logisticspipes.module.ProviderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

// TODO: cant access filter inventory of orderer and the excluded button is not synced in multiplayer
public class ProviderScreenHandler extends ModuleScreenHandler {
    private boolean isExcludeFilter;
    private int extractionMode;

    public ProviderScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);

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
        
        if (this.moduleInventory instanceof ProviderModule module) {
            listener.onPropertyUpdate(this, 0, module.isExcludeFilter() ? 1 : 0);
            listener.onPropertyUpdate(this, 1, module.getExtractionMode().ordinal());
        } else if (this.moduleInventory instanceof ProviderLogisticPipeBlockEntity pipe) {
            listener.onPropertyUpdate(this, 0, pipe.isExcludeFilter() ? 1 : 0);
            listener.onPropertyUpdate(this, 1, pipe.getExtractionMode().ordinal());
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.moduleInventory instanceof ProviderModule module) {
                    if (this.isExcludeFilter != module.isExcludeFilter()) {
                        this.isExcludeFilter = module.isExcludeFilter();
                        listener.onPropertyUpdate(this, 0, module.isExcludeFilter() ? 1 : 0);
                    }

                    if (this.extractionMode != module.getExtractionMode().ordinal()) {
                        this.extractionMode = module.getExtractionMode().ordinal();
                        listener.onPropertyUpdate(this, 1, module.getExtractionMode().ordinal());
                    }
                } else if (this.moduleInventory instanceof ProviderLogisticPipeBlockEntity pipe) {
                    if (this.isExcludeFilter != pipe.isExcludeFilter()) {
                        this.isExcludeFilter = pipe.isExcludeFilter();
                        listener.onPropertyUpdate(this, 0, pipe.isExcludeFilter() ? 1 : 0);
                    }

                    if (this.extractionMode != pipe.getExtractionMode().ordinal()) {
                        this.extractionMode = pipe.getExtractionMode().ordinal();
                        listener.onPropertyUpdate(this, 1, pipe.getExtractionMode().ordinal());
                    }
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        if (this.moduleInventory instanceof ProviderModule module) {
            switch (id) {
                case 0 -> {
                    module.setFilterExcluded(value == 1);
                }

                case 1 -> {
                    module.setExtractionMode(ExtractionMode.values()[value]);
                }
            }
        } else if (this.moduleInventory instanceof ProviderLogisticPipeBlockEntity pipe) {
            switch (id) {
                case 0 -> {
                    pipe.setFilterExcluded(value == 1);
                }

                case 1 -> {
                    pipe.setExtractionMode(ExtractionMode.values()[value]);
                }
            }
        }
    }
}
