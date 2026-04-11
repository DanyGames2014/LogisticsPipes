package net.danygames2014.logisticspipes.module;

public class AdvancedExtractorModuleMk3 extends AdvancedExtractorModuleMk2 {
    public AdvancedExtractorModuleMk3() {
        super();
    }

    @Override
    protected int ticksToAction() {
        return 0;
    }

    protected int itemsToExtract() {
        return 64;
    }
}
