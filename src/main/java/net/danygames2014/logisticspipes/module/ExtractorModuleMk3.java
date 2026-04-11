package net.danygames2014.logisticspipes.module;

public class ExtractorModuleMk3 extends ExtractorModuleMk2 {
    public ExtractorModuleMk3() {
        super();
    }

    @Override
    protected int ticksToAction() {
        return 0;
    }

    @Override
    protected int itemsToExtract() {
        return 64;
    }
}
