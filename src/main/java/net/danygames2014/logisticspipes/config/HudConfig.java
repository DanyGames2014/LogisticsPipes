package net.danygames2014.logisticspipes.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class HudConfig {
    @ConfigEntry(name = "HUD Render Distance", minValue = 4.0F, maxValue = 64.0F)
    public Float hudRenderDistance = 50F;

    @ConfigEntry(name = "Display popup", description = "Set the default configuration for the popup of the Orderer Gui")
    public Boolean displayPopup = true;

    @ConfigEntry(name = "Invert Mouse Wheel", description = "")
    public Boolean invertWheel = false;
}
