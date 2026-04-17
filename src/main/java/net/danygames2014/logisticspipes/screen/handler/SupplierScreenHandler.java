package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.SupplierLogisticPipeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerListener;

public class SupplierScreenHandler extends ModuleScreenHandler {
    public final SupplierLogisticPipeBlockEntity pipe;
    private boolean requestPartial;
    
    public SupplierScreenHandler(PlayerEntity player, SupplierLogisticPipeBlockEntity pipe, Inventory moduleInventory) {
        super(player, moduleInventory);
        this.pipe = pipe;

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
        if (pipe != null) {
            listener.onPropertyUpdate(this, 0, this.pipe.isRequestingPartials() ? 1 : 0);
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.requestPartial != this.pipe.isRequestingPartials()) {
                    this.requestPartial = this.pipe.isRequestingPartials();
                    listener.onPropertyUpdate(this, 0, this.pipe.isRequestingPartials() ? 1 : 0);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        if (id == 0) {
            this.pipe.setRequestingPartials(value == 1);
        }
    }
}
