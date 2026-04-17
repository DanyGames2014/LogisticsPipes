package net.danygames2014.logisticspipes.screen.handler;

import net.danygames2014.logisticspipes.interfaces.SneakyDirectionReceiver;
import net.danygames2014.logisticspipes.util.SneakyDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;

public class ExtractorScreenHandler extends ModuleScreenHandler {
    public final SneakyDirectionReceiver module;
    private int direction;

    public ExtractorScreenHandler(PlayerEntity player, SneakyDirectionReceiver directionReceiver) {
        super(player, null);
        this.module = directionReceiver;
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.module.getSneakyDirection().ordinal());
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.direction != this.module.getSneakyDirection().ordinal()) {
                    this.direction = this.module.getSneakyDirection().ordinal();
                    listener.onPropertyUpdate(this, 0, this.module.getSneakyDirection().ordinal());
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        if (id == 0) {
            this.module.setSneakyDirection(SneakyDirection.values()[value]);
        }
    }
}
