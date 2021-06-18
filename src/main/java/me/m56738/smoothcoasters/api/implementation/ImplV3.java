package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import me.m56738.smoothcoasters.api.NetworkInterface;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class ImplV3 extends ImplV2 {
    protected static final String CHANNEL_ENTITY_PROPERTIES = "smoothcoasters:eprops";
    private final Plugin plugin;
    private final EnumSet<Feature> features = EnumSet.of(Feature.ENTITY_PROPERTIES);

    public ImplV3(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.features.addAll(super.getFeatures());
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ENTITY_PROPERTIES);
    }

    @Override
    public EnumSet<Feature> getFeatures() {
        return features;
    }

    @Override
    public byte getVersion() {
        return 3;
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
}
