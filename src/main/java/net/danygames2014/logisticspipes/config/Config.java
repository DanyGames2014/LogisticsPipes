package net.danygames2014.logisticspipes.config;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class Config {
    @ConfigRoot(value = "network", visibleName = "Network Config", index = 0)
    public static final NetworkConfig NETWORK_CONFIG = new NetworkConfig();
    
    @ConfigRoot(value = "hud", visibleName = "HUD Config", index = 1)
    public static final HudConfig HUD_CONFIG = new HudConfig();
}
