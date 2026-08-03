package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.event.PlayerSmoothCoastersHandshakeEvent;
import me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerListener implements Listener, PluginMessageListener {
    private static final String CHANNEL = "smoothcoasters:hs";
    private static final String SUPPORTED_VERSIONS = "smoothcoasters_supported_versions";
    private static final String OFFERED_VERSIONS = "smoothcoasters_offered_versions";
    private final SmoothCoastersAPI api;

    public PlayerListener(SmoothCoastersAPI api) {
        this.api = api;
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
        Bukkit.getMessenger().registerIncomingPluginChannel(api.getPlugin(), CHANNEL, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(api.getPlugin(), CHANNEL);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinStart(PlayerJoinEvent event) {
        // Store the supported versions on the player
        event.getPlayer().setMetadata(SUPPORTED_VERSIONS,
                new FixedMetadataValue(api.getPlugin(), api.getImplementations().keySet()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getListeningPluginChannels().contains(CHANNEL)) {
            startHandshake(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        if (event.getChannel().equals(CHANNEL)) {
            startHandshake(event.getPlayer());
        }
    }

    private void startHandshake(Player player) {
        if (player.hasMetadata(OFFERED_VERSIONS)) {
            // Another plugin already started the handshake
            return;
        }

        // Determine the set of versions which all plugins support
        Set<Byte> versions = null;
        for (MetadataValue value : player.getMetadata(SUPPORTED_VERSIONS)) {
            @SuppressWarnings("unchecked")
            Set<Byte> set = (Set<Byte>) value.value();

            if (versions == null) {
                // initialize versions
                versions = new TreeSet<>(set);
            } else {
                // remove unsupported versions
                versions.retainAll(set);
            }
        }

        // Store the offered versions on the player
        // Causes other plugins which contain SmoothCoastersAPI to skip repeating this step (see early return above)
        player.setMetadata(OFFERED_VERSIONS, new FixedMetadataValue(api.getPlugin(), versions));

        if (versions == null || versions.isEmpty()) {
            api.getPlugin().getLogger().warning(
                    "[SmoothCoastersAPI] Plugins have no supported SmoothCoasters versions in common");
            for (MetadataValue value : player.getMetadata(SUPPORTED_VERSIONS)) {
                @SuppressWarnings("unchecked")
                Set<Byte> set = (Set<Byte>) value.value();
                api.getPlugin().getLogger().warning("[SmoothCoastersAPI] " + value.getOwningPlugin().getName() + ": " +
                        set.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }
            return;
        }

        byte[] message = new byte[versions.size() + 1];
        message[0] = (byte) versions.size();

        int i = 1;
        for (Byte version : versions) {
            message[i++] = version;
        }

        player.sendPluginMessage(api.getPlugin(), CHANNEL, message);
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
        Player player = event.getPlayer();
        player.removeMetadata(SUPPORTED_VERSIONS, api.getPlugin());
        player.removeMetadata(OFFERED_VERSIONS, api.getPlugin());
        api.removeEntry(player);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(api.getPlugin(), CHANNEL, this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(api.getPlugin(), CHANNEL);
    }
}
