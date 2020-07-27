package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.implementation.ImplV1;
import me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SmoothCoastersAPI {
    private final Plugin plugin;
    private final PlayerListener playerListener;
    private final Map<Byte, Implementation> implementations = new HashMap<>();
    private final Map<Player, Implementation> players = new HashMap<>();

    public SmoothCoastersAPI(Plugin plugin) {
        this.plugin = plugin;
        this.playerListener = new PlayerListener(this);
        registerImplementation(new ImplV1(plugin));
    }

    public void registerImplementation(Implementation implementation) {
        implementations.put(implementation.getVersion(), implementation);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Map<Byte, Implementation> getImplementations() {
        return implementations;
    }

    void setImplementation(Player player, Implementation implementation) {
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
        Implementation implementation = players.get(player);
        if (implementation == null) {
            return false;
        }

        implementation.sendRotation(player, x, y, z, w, ticks);
        return true;
    }

    public boolean sendBulk(Player player, byte[] data) {
        Implementation implementation = players.get(player);
        if (implementation == null) {
            return false;
        }

        implementation.sendBulk(player, data);
        return true;
    }

    public void unregister() {
        implementations.clear();
        playerListener.unregister();
    }
}
