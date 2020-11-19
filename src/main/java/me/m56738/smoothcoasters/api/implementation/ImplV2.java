package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class ImplV2 extends ImplV1 {
    protected static final String CHANNEL_ENTITY_ROTATION = "smoothcoasters:erot";
    private final Plugin plugin;
    private final EnumSet<Feature> features = EnumSet.of(Feature.ENTITY_ROTATION);

    public ImplV2(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.features.addAll(super.getFeatures());
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ENTITY_ROTATION);
    }

    @Override
    public EnumSet<Feature> getFeatures() {
        return features;
    }

    @Override
    public byte getVersion() {
        return 2;
    }

    @Override
    public void sendEntityRotation(Player player, int entity, float x, float y, float z, float w, byte ticks) {
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

        player.sendPluginMessage(plugin, CHANNEL_ENTITY_ROTATION, bytes);
    }
}
