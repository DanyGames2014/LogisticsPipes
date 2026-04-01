package net.danygames2014.logisticspipes.gui;

public abstract class SmallColorRenderSlot implements RenderSlot{
    public abstract int getColor();
    public abstract boolean drawColor();
    @Override
    public int getSize() {
        return 8;
    }
}
