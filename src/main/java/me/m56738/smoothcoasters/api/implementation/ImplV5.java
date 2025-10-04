package me.m56738.smoothcoasters.api.implementation;

import me.m56738.smoothcoasters.api.Feature;
import me.m56738.smoothcoasters.api.NetworkInterface;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteBuffer;

@Deprecated
public class ImplV5 extends ImplV6 {
    protected static final String CHANNEL_ENTITY_ROTATION = "smoothcoasters:erot";
    protected static final String CHANNEL_ENTITY_PROPERTIES = "smoothcoasters:eprop";

    public ImplV5(Plugin plugin) {
        super(plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ENTITY_ROTATION);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_ENTITY_PROPERTIES);
        this.features.add(Feature.ENTITY_ROTATION);
        this.features.add(Feature.ENTITY_PROPERTIES);
    }

    @Override
    public byte getVersion() {
        return 5;
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
}
