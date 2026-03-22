package net.danygames2014.logisticspipes.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class HudConfig {
    @ConfigEntry(name = "HUD Render Distance", minValue = 4.0F, maxValue = 64.0F)
    public Float hudRenderDistance = 50F;
}
