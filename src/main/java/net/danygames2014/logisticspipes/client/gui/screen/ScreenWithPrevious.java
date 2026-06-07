package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ScreenIdentifierProvider;
import net.danygames2014.logisticspipes.screen.handler.ModuleScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;

public class ScreenWithPrevious extends LogisticsBaseScreen {
    private final Screen previousScreen;

    public ScreenWithPrevious(PlayerEntity player, LogisticPipeBlockEntity pipe, ModuleScreenHandler container, Screen previousScreen) {
        super(player, pipe, container);
        this.previousScreen = previousScreen;
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (keyCode == 1 || character == 'e') {
            if (previousScreen instanceof ScreenIdentifierProvider provider) {
                super.keyPressed(character, keyCode);
                if (!player.world.isRemote) {
                    minecraft.setScreen(previousScreen);
                }
                //GuiHelper.openGUI(minecraft.player, provider.getScreenIdentifier(), blockEntity, moduleHandler);
            } else {
                super.keyPressed(character, keyCode);
            }
        }

    }
}
