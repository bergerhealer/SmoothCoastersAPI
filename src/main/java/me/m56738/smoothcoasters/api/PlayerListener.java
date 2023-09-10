package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.event.PlayerSmoothCoastersHandshakeEvent;
import me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;

public class PlayerListener implements Listener, PluginMessageListener {
    private static final String CHANNEL = "smoothcoasters:hs";
    private final SmoothCoastersAPI api;

    public PlayerListener(SmoothCoastersAPI api) {
        this.api = api;
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
        Bukkit.getMessenger().registerIncomingPluginChannel(api.getPlugin(), CHANNEL, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(api.getPlugin(), CHANNEL);
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals(CHANNEL)) {
            return;
        }

        // Client is using SmoothCoasters - tell it which implementation versions we support

        Map<Byte, Implementation> implementations = api.getImplementations();

        byte[] message = new byte[implementations.size() + 1];
        message[0] = (byte) implementations.size();

        int i = 1;
        for (Byte version : implementations.keySet()) {
            message[i++] = version;
        }

        event.getPlayer().sendPluginMessage(api.getPlugin(), CHANNEL, message);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] payload) {
        if (!channel.equals(CHANNEL) || payload.length < 1) {
            return;
        }

        // The client decided which implementation should be used

        Implementation implementation;
        String version;
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        try {
            implementation = api.getImplementations().get(buffer.get());
            version = buffer.hasRemaining() ? Util.readString(buffer, 32) : null;
        } catch (Exception e) {
            api.getPlugin().getLogger().log(Level.SEVERE, "[SmoothCoastersAPI] Received invalid handshake from " + player.getName(), e);
            return;
        }

        PlayerEntry entry = api.getOrCreateEntry(player);
        entry.setImplementation(implementation);
        entry.setVersion(version);

        if (implementation != null) {
            Bukkit.getPluginManager().callEvent(new PlayerSmoothCoastersHandshakeEvent(player, implementation, version));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        api.removeEntry(event.getPlayer());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(api.getPlugin(), CHANNEL, this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(api.getPlugin(), CHANNEL);
    }
}
