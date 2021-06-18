package me.m56738.smoothcoasters.api;

import org.bukkit.entity.Player;

public interface NetworkInterface {
    void sendMessage(Player player, String channel, byte[] data);
}
