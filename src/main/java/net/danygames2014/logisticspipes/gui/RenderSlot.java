package net.danygames2014.logisticspipes.gui;

public interface RenderSlot {
    void mouseClicked(int button);

    boolean drawSlotBackground();

    int getXPos();

    int getYPos();

    String getToolTipText();

    boolean displayToolTip();

    int getSize();
}
