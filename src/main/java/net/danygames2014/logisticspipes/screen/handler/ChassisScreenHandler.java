package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.block.entity.ChassisLogisticPipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ChassisScreenHandler extends ModuleScreenHandler {
    public final ChassisLogisticPipeBlockEntity pipe;
    
    public ChassisScreenHandler(PlayerEntity player, ChassisLogisticPipeBlockEntity pipe) {
        super(player, pipe.getModuleInventory());
        this.pipe = pipe;

        if (pipe.getChassisSize() < 5) {
            addNormalSlotsForPlayerInventory(18, 97);
        } else {
            addNormalSlotsForPlayerInventory(18, 174);
        }
        
        // Module Slots
        if (pipe.getChassisSize() > 0) {
            addModuleSlot(0, moduleInventory, 19, 9, pipe);
        }
        
        if (pipe.getChassisSize() > 1) {
            addModuleSlot(1, moduleInventory, 19, 29, pipe);
        }
        
        if (pipe.getChassisSize() > 2) {
            addModuleSlot(2, moduleInventory, 19, 49, pipe);
        }
        
        if (pipe.getChassisSize() > 3) {
            addModuleSlot(3, moduleInventory, 19, 69, pipe);
        }
        
        if (pipe.getChassisSize() > 4) {
            addModuleSlot(4, moduleInventory, 19, 89, pipe);
            addModuleSlot(5, moduleInventory, 19, 109, pipe);
            addModuleSlot(6, moduleInventory, 19, 129, pipe);
            addModuleSlot(7, moduleInventory, 19, 149, pipe);
        }
    }
}
