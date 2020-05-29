package me.m56738.smoothcoasters.api.network;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;

public class NetworkV1 implements NetworkImplementation {
    private static final String CHANNEL = "smoothcoasters:rotation";
    private final Plugin plugin;

    public NetworkV1(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
    }

    @Override
    public byte getVersion() {
        return 1;
    }

    @Override
    public void sendRotation(Player player, float x, float y, float z, float w, byte ticks) {
        ByteBuffer buffer = ByteBuffer.allocate(17);
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
        buffer.put(ticks);

        buffer.rewind();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        player.sendPluginMessage(plugin, CHANNEL, bytes);
    }
}
