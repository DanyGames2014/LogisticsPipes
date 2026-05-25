package net.danygames2014.logisticspipes.gui.popup;

import net.danygames2014.logisticspipes.client.gui.screen.NormalOrderScreenMk2;
import net.danygames2014.logisticspipes.gui.SubScreen;

public class DiskPopupSubScreen extends SubScreen {
    NormalOrderScreenMk2 mainScreen;

    public DiskPopupSubScreen(NormalOrderScreenMk2 mainScreen) {
        super(150, 200, 0, 0);
        this.mainScreen = mainScreen;
    }
}
