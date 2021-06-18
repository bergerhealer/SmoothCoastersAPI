package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import me.m56738.smoothcoasters.api.NetworkInterface;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class ImplV1 implements Implementation {
    protected static final String CHANNEL_ROTATION = "smoothcoasters:rot";
    protected static final String CHANNEL_BULK = "smoothcoasters:bulk";
    private final Plugin plugin;
    private final EnumSet<Feature> features = EnumSet.of(Feature.ROTATION, Feature.BULK);

    public ImplV1(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ROTATION);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_BULK);
    }

    @Override
    public EnumSet<Feature> getFeatures() {
        return features;
    }

    @Override
    public byte getVersion() {
        return 1;
    }

    @Override
    public void sendRotation(NetworkInterface network, Player player, float x, float y, float z, float w, byte ticks) {
        ByteBuffer buffer = ByteBuffer.allocate(17);
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
        buffer.put(ticks);

        buffer.rewind();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        network.sendMessage(player, CHANNEL_ROTATION, bytes);
    }

    @Override
    public void sendBulk(NetworkInterface network, Player player, byte[] data) {
        network.sendMessage(player, CHANNEL_BULK, data);
    }
}
