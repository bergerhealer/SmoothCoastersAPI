package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public interface Implementation {
    default boolean isSupported(Feature feature) {
        return getFeatures().contains(feature);
    }

    EnumSet<Feature> getFeatures();

    byte getVersion();

    default void sendRotation(Player player, float x, float y, float z, float w, byte ticks) {
        throw new UnsupportedOperationException();
    }

    default void sendBulk(Player player, byte[] data) {
        throw new UnsupportedOperationException();
    }

    default void sendEntityRotation(Player player, int entity, float x, float y, float z, float w, byte ticks) {
        throw new UnsupportedOperationException();
    }
}
