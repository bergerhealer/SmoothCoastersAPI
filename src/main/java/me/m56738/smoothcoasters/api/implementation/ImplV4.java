package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import me.m56738.smoothcoasters.api.NetworkInterface;
import me.m56738.smoothcoasters.api.RotationMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class ImplV4 extends ImplV3 {
    protected static final String CHANNEL_ROTATION_LIMIT = "smoothcoasters:limit";
    private final Plugin plugin;
    private final EnumSet<Feature> features = EnumSet.of(Feature.ROTATION_LIMIT);

    @SuppressWarnings("deprecation")
    public ImplV4(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.features.addAll(super.getFeatures());
        this.features.remove(Feature.BULK);
        this.features.remove(Feature.ROTATION_MODE);
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
    public void sendBulk(NetworkInterface network, Player player, byte[] data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRotationMode(NetworkInterface network, Player player, RotationMode mode) {
        throw new UnsupportedOperationException();
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
