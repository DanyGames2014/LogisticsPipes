package net.danygames2014.logisticspipes.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class HudConfig {
    @ConfigEntry(name = "HUD Render Distance", minValue = 4.0F, maxValue = 64.0F)
    public Float hudRenderDistance = 20F;

    @ConfigEntry(name = "Display popup", description = "Set the default configuration for the popup of the Orderer Gui")
    public Boolean displayPopup = true;

    @ConfigEntry(name = "Cap request add button amount", description = "Makes add button in request screen not order more than you have. can be ignored with shift")
    public Boolean capAddAmount = true;

    @ConfigEntry(name = "Invert Mouse Wheel", description = "")
    public Boolean invertWheel = false;

    @ConfigEntry(name = "Show crafting hud", description = "Whether or not to show the crafting hud when wearing hud glasses")
    public Boolean craftingHud = true;

    @ConfigEntry(name = "Show chassis hud", description = "Whether or not to show the chassis hud when wearing hud glasses")
    public Boolean chassisHud = true;

    @ConfigEntry(name = "Show provider hud", description = "Whether or not to show the provider hud when wearing hud glasses")
    public Boolean providerHud = true;

    @ConfigEntry(name = "Show satellite hud", description = "Whether or not to show the satellite hud when wearing hud glasses")
    public Boolean satelliteHud = true;
}
