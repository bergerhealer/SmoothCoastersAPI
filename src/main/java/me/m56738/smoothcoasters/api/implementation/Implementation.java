package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import me.m56738.smoothcoasters.api.NetworkInterface;
import me.m56738.smoothcoasters.api.RotationMode;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public interface Implementation {
    default boolean isSupported(Feature feature) {
        return getFeatures().contains(feature);
    }

    EnumSet<Feature> getFeatures();

    byte getVersion();

    default void sendRotation(NetworkInterface network, Player player, float x, float y, float z, float w, byte ticks) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default void sendBulk(NetworkInterface network, Player player, byte[] data) {
        throw new UnsupportedOperationException();
    }

    default void sendEntityRotation(NetworkInterface network, Player player, int entity, float x, float y, float z, float w, byte ticks) {
        throw new UnsupportedOperationException();
    }

    default void sendEntityProperties(NetworkInterface network, Player player, int entity, byte ticks) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default void sendRotationMode(NetworkInterface network, Player player, RotationMode mode) {
        throw new UnsupportedOperationException();
    }

    default void sendRotationLimit(NetworkInterface network, Player player, float minYaw, float maxYaw, float minPitch, float maxPitch) {
        throw new UnsupportedOperationException();
    }
}
