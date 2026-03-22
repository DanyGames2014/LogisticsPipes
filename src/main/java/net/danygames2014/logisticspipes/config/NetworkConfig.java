package net.danygames2014.logisticspipes.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class NetworkConfig {
    @ConfigEntry(name = "Neighbor Detection Frequency", minValue = 10, maxValue = 100)
    public Integer neighborDetectionFrequency = 20;
    
    @ConfigEntry(name = "Neighbor Detection Distance", minValue = 8, maxValue = 128)
    public Integer neighborDetectionDistance = 64;
}
