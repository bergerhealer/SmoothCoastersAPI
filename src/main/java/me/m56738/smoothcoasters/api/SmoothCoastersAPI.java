package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.implementation.ImplV4;
import me.m56738.smoothcoasters.api.implementation.ImplV5;
import me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SmoothCoastersAPI {
    private final Plugin plugin;
    private final PlayerListener playerListener;
    private final Map<Byte, Implementation> implementations = new HashMap<>();
    private final Map<UUID, PlayerEntry> players = new ConcurrentHashMap<>();
    private final NetworkInterface defaultNetwork;

    public SmoothCoastersAPI(Plugin plugin) {
        this.plugin = plugin;
        this.playerListener = new PlayerListener(this);
        this.defaultNetwork = new DefaultNetworkInterface(plugin);
        registerImplementation(new ImplV4(plugin));
        registerImplementation(new ImplV5(plugin));
    }

    public void registerImplementation(Implementation implementation) {
        implementations.put(implementation.getVersion(), implementation);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    Map<Byte, Implementation> getImplementations() {
        return implementations;
    }

    PlayerEntry getEntry(Player player) {
        return players.get(player.getUniqueId());
    }

    PlayerEntry getOrCreateEntry(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), u -> new PlayerEntry());
    }

    void removeEntry(Player player) {
        players.remove(player.getUniqueId());
    }

    private Implementation getImplementation(Player player) {
        PlayerEntry entry = getEntry(player);
        if (entry == null) {
            return null;
        }
        return entry.getImplementation();
    }

    public boolean isEnabled(Player player) {
        return getImplementation(player) != null;
    }

    /**
     * Returns the version of the implementation used for the player.
     *
     * @param player player whose version should be queried
     * @return the implementation version, or -1 if none is used
     */
    public byte getVersion(Player player) {
        Implementation implementation = getImplementation(player);
        if (implementation != null) {
            return implementation.getVersion();
        } else {
            return -1;
        }
    }

    /**
     * Returns the version of the mod that a player has installed.
     *
     * @param player player whose version should be queried
     * @return the mod version, or null if unknown or not installed
     */
    public String getModVersion(Player player) {
        PlayerEntry entry = getEntry(player);
        if (entry != null) {
            return entry.getVersion();
        } else {
            return null;
        }
    }

    /**
     * Checks if a feature can be used for a player.
     *
     * @param player  player who should support the feature
     * @param feature feature which should be supported
     * @return true if the feature is supported
     */
    public boolean isSupported(Player player, Feature feature) {
        Implementation implementation = getImplementation(player);
        if (implementation == null) {
            return false;
        }
        return implementation.isSupported(feature);
    }

    /**
     * Resets the camera rotation of the player.
     * Should be called when the player leaves a vehicle.
     *
     * @param network interface to communicate with the player, null for default
     * @param player  player whose view should be reset
     * @return true if the implementation used for this player supports {@link Feature#ROTATION}
     */
    public boolean resetRotation(NetworkInterface network, Player player) {
        return setRotation(network, player, 0, 0, 0, 1, (byte) 0);
    }

    /**
     * Rotates the camera of the player.
     *
     * @param network interface to communicate with the player, null for default
     * @param player  player whose view should be rotated
     * @param x       x component of the quaternion
     * @param y       y component of the quaternion
     * @param z       z component of the quaternion
     * @param w       w component of the quaternion
     * @param ticks   how long the interpolation should take - usually 3 ticks
     * @return true if the implementation used for this player supports {@link Feature#ROTATION}
     */
    public boolean setRotation(NetworkInterface network, Player player, float x, float y, float z, float w, byte ticks) {
        if (network == null) {
            network = defaultNetwork;
        }

        Implementation implementation = getImplementation(player);
        if (implementation == null || !implementation.isSupported(Feature.ROTATION)) {
            return false;
        }

        implementation.sendRotation(network, player, x, y, z, w, ticks);
        return true;
    }

    /**
     * Rotates another entity for the player.
     *
     * @param network interface to communicate with the player, null for default
     * @param player  player who should see the rotated entity
     * @param entity  entity which should be rotated
     * @param x       x component of the quaternion
     * @param y       y component of the quaternion
     * @param z       z component of the quaternion
     * @param w       w component of the quaternion
     * @param ticks   how long the interpolation should take - usually 3 ticks
     * @return true if the implementation used for this player supports {@link Feature#ENTITY_ROTATION}
     */
    public boolean setEntityRotation(NetworkInterface network, Player player, int entity, float x, float y, float z, float w, byte ticks) {
        if (network == null) {
            network = defaultNetwork;
        }

        Implementation implementation = getImplementation(player);
        if (implementation == null || !implementation.isSupported(Feature.ENTITY_ROTATION)) {
            return false;
        }

        implementation.sendEntityRotation(network, player, entity, x, y, z, w, ticks);
        return true;
    }

    /**
     * Sets the lerp ticks of the specified entity.
     * The entity must be an armor stand.
     * The default value is 3.
     *
     * @param network interface to communicate with the player, null for default
     * @param player  player who the change should be sent to
     * @param entity  entity which should be modified
     * @param ticks   lerp ticks of the entity
     * @return true if the implementation used for this player supports {@link Feature#ENTITY_PROPERTIES}
     */
    public boolean setEntityLerpTicks(NetworkInterface network, Player player, int entity, byte ticks) {
        if (network == null) {
            network = defaultNetwork;
        }

        Implementation implementation = getImplementation(player);
        if (implementation == null || !implementation.isSupported(Feature.ENTITY_PROPERTIES)) {
            return false;
        }

        implementation.sendEntityProperties(network, player, entity, ticks);
        return true;
    }

    /**
     * Sets the camera rotation limit for the specified player.
     * This limit is applied to the local yaw/pitch.
     * For example, if all values are 0, the player rotation is locked to the server-supplied rotation set
     * using {@link SmoothCoastersAPI#setRotation}.
     *
     * @param network  interface to communicate with the player, null for default
     * @param player   player who the change should be sent to
     * @param minYaw   the minimum yaw (-180 to 180)
     * @param maxYaw   the maximum yaw (-180 to 180)
     * @param minPitch the minimum pitch (-90 to 90)
     * @param maxPitch the maximum pitch (-90 to 90)
     * @return true if the implementation used for this player supports {@link Feature#ROTATION_LIMIT}
     */
    public boolean setRotationLimit(NetworkInterface network, Player player, float minYaw, float maxYaw, float minPitch, float maxPitch) {
        if (network == null) {
            network = defaultNetwork;
        }

        Implementation implementation = getImplementation(player);
        if (implementation == null || !implementation.isSupported(Feature.ROTATION_LIMIT)) {
            return false;
        }

        implementation.sendRotationLimit(network, player, minYaw, maxYaw, minPitch, maxPitch);
        return true;
    }

    /**
     * Resets (removes) the camera rotation limit for the specified player.
     *
     * @param network interface to communicate with the player, null for default
     * @param player  player who the change should be sent to
     * @return true if the implementation used for this player supports {@link Feature#ROTATION_LIMIT}
     */
    public boolean resetRotationLimit(NetworkInterface network, Player player) {
        return setRotationLimit(network, player, -180f, 180f, -90f, 90f);
    }

    public void unregister() {
        implementations.clear();
        playerListener.unregister();
        players.clear();
    }
}
