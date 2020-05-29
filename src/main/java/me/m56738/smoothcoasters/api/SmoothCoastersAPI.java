package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.network.NetworkImplementation;
import me.m56738.smoothcoasters.api.network.NetworkV1;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SmoothCoastersAPI {
    private final Plugin plugin;
    private final PlayerListener playerListener;
    private final Map<Byte, NetworkImplementation> implementations = new HashMap<>();
    private final Map<Player, NetworkImplementation> players = new HashMap<>();

    public SmoothCoastersAPI(Plugin plugin) {
        this.plugin = plugin;
        this.playerListener = new PlayerListener(this);
        registerImplementation(new NetworkV1(plugin));
    }

    public void registerImplementation(NetworkImplementation implementation) {
        implementations.put(implementation.getVersion(), implementation);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Map<Byte, NetworkImplementation> getImplementations() {
        return implementations;
    }

    public void setImplementation(Player player, NetworkImplementation implementation) {
        if (implementation != null) {
            players.put(player, implementation);
        } else {
            players.remove(player);
        }
    }

    public boolean isEnabled(Player player) {
        return players.containsKey(player);
    }

    public boolean resetRotation(Player player) {
        return setRotation(player, 0, 0, 0, 1, (byte) 0);
    }

    public boolean setRotation(Player player, float x, float y, float z, float w, byte ticks) {
        NetworkImplementation implementation = players.get(player);
        if (implementation == null) {
            return false;
        }

        implementation.sendRotation(player, x, y, z, w, ticks);
        return true;
    }

    public void unregister() {
        implementations.clear();
        playerListener.unregister();
    }
}
