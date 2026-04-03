package net.danygames2014.logisticspipes.client.gui.screen;

import net.danygames2014.logisticspipes.block.entity.LogisticPipeBlockEntity;
import net.danygames2014.logisticspipes.interfaces.ScreenIdentifierProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;

public class ScreenWithPrevious extends LogisticsBaseScreen{
    protected LogisticPipeBlockEntity pipe;
    private Screen previousScreen;

    public ScreenWithPrevious(ScreenHandler container, LogisticPipeBlockEntity pipe, Screen previousScreen){
        super(container);

        this.pipe = pipe;
        this.previousScreen = previousScreen;

    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if(keyCode == 1 || character == 'e'){
            if(previousScreen instanceof ScreenIdentifierProvider provider){
                super.keyPressed(character, keyCode);
                GuiHelper.openGUI(minecraft.player, provider.getScreenIdentifier(), pipe, null);
            } else {
                super.keyPressed(character, keyCode);
            }
        }

    }
}
