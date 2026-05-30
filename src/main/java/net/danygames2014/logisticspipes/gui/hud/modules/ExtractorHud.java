package net.danygames2014.logisticspipes.gui.hud.modules;

import net.danygames2014.logisticspipes.interfaces.HUDButton;
import net.danygames2014.logisticspipes.interfaces.HUDModuleRenderer;
import net.danygames2014.logisticspipes.module.ExtractorModule;
import net.danygames2014.logisticspipes.util.SneakyDirection;
import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

public class ExtractorHud implements HUDModuleRenderer {
    private final ExtractorModule module;

    public ExtractorHud(ExtractorModule module) {
        this.module = module;
    }

    @Override
    public void renderContent() {
        Minecraft mc = Minecraft.INSTANCE;

        SneakyDirection d = module.getSneakyDirection();
        mc.textRenderer.draw("Extract" , -22, -22, 0);
        mc.textRenderer.draw("from:" , -22, -9, 0);
        mc.textRenderer.draw(d.name() , -22, 18, 0);
    }

    @Override
    public List<HUDButton> getButtons() {
        return null;
    }
}
