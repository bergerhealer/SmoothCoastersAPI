package me.m56738.smoothcoasters.api.implementation;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class ImplV1 implements Implementation {
    private static final String CHANNEL_ROTATION = "smoothcoasters:rot";
    private static final String CHANNEL_BULK = "smoothcoasters:bulk";
    private final Plugin plugin;

    public ImplV1(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ROTATION);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_BULK);
    }

    @Override
    public EnumSet<Feature> getFeatures() {
        return EnumSet.of(Feature.ROTATION, Feature.BULK);
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

        player.sendPluginMessage(plugin, CHANNEL_ROTATION, bytes);
    }

    @Override
    public void sendBulk(Player player, byte[] data) {
        player.sendPluginMessage(plugin, CHANNEL_BULK, data);
    }
}
