package net.danygames2014.logisticspipes.interfaces;

public interface HUDButton {
    int getX();
    int getY();
    int sizeX();
    int sizeY();
    void setFocused();
    boolean isFocused();
    void clearFocused();
    void blockFocused();
    boolean isblockFocused();
    int focusedTime();
    void clicked();
    void renderButton(boolean hover, boolean clicked);
    void renderAlways();
    boolean shouldRenderButton();
    boolean buttonEnabled();
}
