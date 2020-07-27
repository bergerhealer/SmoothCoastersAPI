package me.m56738.smoothcoasters.api.implementation;

import org.bukkit.entity.Player;

import java.util.EnumSet;

public interface Implementation {
    default boolean isSupported(Feature feature) {
        return getFeatures().contains(feature);
    }

    EnumSet<Feature> getFeatures();

    byte getVersion();

    void sendRotation(Player player, float x, float y, float z, float w, byte ticks);

    void sendBulk(Player player, byte[] data);
}
