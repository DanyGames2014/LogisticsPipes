package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.SatelliteLogisticPipeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

public class SatelliteScreenHandler extends ModuleScreenHandler {
    public final SatelliteLogisticPipeBlockEntity satellite;
    private int satelliteId = 0;

    public SatelliteScreenHandler(PlayerEntity player, Inventory moduleInventory) {
        super(player, moduleInventory);
        this.satellite = (SatelliteLogisticPipeBlockEntity) moduleInventory;
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.satellite.satelliteId);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.satelliteId != this.satellite.satelliteId) {
                    this.satelliteId = this.satellite.satelliteId;
                    listener.onPropertyUpdate(this, 0, this.satellite.satelliteId);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        if (id == 0) {
            this.satellite.satelliteId = value;
        }
    }
}
