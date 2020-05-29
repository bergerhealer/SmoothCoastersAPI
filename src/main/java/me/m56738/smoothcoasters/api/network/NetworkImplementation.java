package me.m56738.smoothcoasters.api.network;

import org.bukkit.entity.Player;

public interface NetworkImplementation {
    byte getVersion();

    void sendRotation(Player player, float x, float y, float z, float w, byte ticks);
}
