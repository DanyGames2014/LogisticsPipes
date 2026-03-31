package net.danygames2014.logisticspipes.gui;

import net.danygames2014.logisticspipes.client.gui.screen.LogisticsBaseScreen;

public interface SubScreenController {
    void setSubScreen(SubScreen gui);

    void resetSubScreen();

    boolean hasSubScreen();

    SubScreen getSubScreen();

    LogisticsBaseScreen getBaseScreen();
}
