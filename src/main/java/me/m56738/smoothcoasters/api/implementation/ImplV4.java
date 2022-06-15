package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import me.m56738.smoothcoasters.api.NetworkInterface;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class ImplV4 implements Implementation {
    protected static final String CHANNEL_ROTATION = "smoothcoasters:rot";
    protected static final String CHANNEL_ENTITY_ROTATION = "smoothcoasters:erot";
    protected static final String CHANNEL_ENTITY_PROPERTIES = "smoothcoasters:eprop";
    protected static final String CHANNEL_ROTATION_LIMIT = "smoothcoasters:limit";
    private final Plugin plugin;
    private final EnumSet<Feature> features = EnumSet.of(
            Feature.ROTATION, Feature.ENTITY_ROTATION, Feature.ENTITY_PROPERTIES, Feature.ROTATION_LIMIT
    );

    public ImplV4(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ROTATION);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ENTITY_ROTATION);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ENTITY_PROPERTIES);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ROTATION_LIMIT);
    }

    @Override
    public EnumSet<Feature> getFeatures() {
        return features;
    }

    @Override
    public byte getVersion() {
        return 4;
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
    public void sendEntityRotation(NetworkInterface network, Player player, int entity, float x, float y, float z, float w, byte ticks) {
        ByteBuffer buffer = ByteBuffer.allocate(21);
        buffer.putInt(entity);
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
        buffer.put(ticks);

        buffer.rewind();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        network.sendMessage(player, CHANNEL_ENTITY_ROTATION, bytes);
    }

    @Override
    public void sendEntityProperties(NetworkInterface network, Player player, int entity, byte ticks) {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.putInt(entity);
        buffer.put(ticks);

        buffer.rewind();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        network.sendMessage(player, CHANNEL_ENTITY_PROPERTIES, bytes);
    }

    @Override
    public void sendRotationLimit(NetworkInterface network, Player player, float minYaw, float maxYaw, float minPitch, float maxPitch) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putFloat(minYaw);
        buffer.putFloat(maxYaw);
        buffer.putFloat(minPitch);
        buffer.putFloat(maxPitch);

        buffer.rewind();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        network.sendMessage(player, CHANNEL_ROTATION_LIMIT, bytes);
    }
}
