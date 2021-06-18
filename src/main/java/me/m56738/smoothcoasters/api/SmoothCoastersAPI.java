package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.implementation.ImplV1;
import me.m56738.smoothcoasters.api.implementation.ImplV2;
import me.m56738.smoothcoasters.api.implementation.ImplV3;
import me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmoothCoastersAPI {
    private final Plugin plugin;
    private final PlayerListener playerListener;
    private final Map<Byte, Implementation> implementations = new HashMap<>();
    private final Map<Player, Implementation> players = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final NetworkInterface defaultNetwork;

    public SmoothCoastersAPI(Plugin plugin) {
        this.plugin = plugin;
        this.playerListener = new PlayerListener(this);
        this.defaultNetwork = new DefaultNetworkInterface(plugin);
        registerImplementation(new ImplV1(plugin));
        registerImplementation(new ImplV2(plugin));
        registerImplementation(new ImplV3(plugin));
    }

    public void registerImplementation(Implementation implementation) {
        writeLock.lock();
        try {
            implementations.put(implementation.getVersion(), implementation);
        } finally {
            writeLock.unlock();
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    Map<Byte, Implementation> getImplementations() {
        readLock.lock();
        try {
            return new HashMap<>(implementations);
        } finally {
            readLock.unlock();
        }
    }

    void setImplementation(Player player, Implementation implementation) {
        writeLock.lock();
        try {
            if (implementation != null) {
                players.put(player, implementation);
            } else {
                players.remove(player);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isEnabled(Player player) {
        readLock.lock();
        try {
            return players.containsKey(player);
        } finally {
            readLock.unlock();
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
        readLock.lock();
        try {
            Implementation implementation = players.get(player);
            if (implementation == null) {
                return false;
            }
            return implementation.isSupported(feature);
        } finally {
            readLock.unlock();
        }
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

        readLock.lock();
        try {
            Implementation implementation = players.get(player);
            if (implementation == null || !implementation.isSupported(Feature.ROTATION)) {
                return false;
            }

            implementation.sendRotation(network, player, x, y, z, w, ticks);
            return true;
        } finally {
            readLock.unlock();
        }
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

        readLock.lock();
        try {
            Implementation implementation = players.get(player);
            if (implementation == null || !implementation.isSupported(Feature.ENTITY_ROTATION)) {
                return false;
            }

            implementation.sendEntityRotation(network, player, entity, x, y, z, w, ticks);
            return true;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Sends a bulk packet to the player.
     * A bulk packet contains multiple normal packets.
     * All of those packets will be executed in the same tick.
     * <p>
     * <b>No packets will be sent if the implementation used for this player doesn't support {@link Feature#BULK}.</b>
     * <p>
     * This method can (and should) be called asynchronously.
     * For performance reasons, you should check whether the feature is supported
     * using {@link #isSupported(Player, Feature)} before encoding the data.
     *
     * @param network interface to communicate with the player, null for default
     * @param player  player who should receive the packets
     * @param data    encoded packet data
     * @return true if the implementation used for this player supports {@link Feature#BULK}, <b>send the packets yourself if false!</b>
     */
    public boolean sendBulk(NetworkInterface network, Player player, byte[] data) {
        if (network == null) {
            network = defaultNetwork;
        }

        readLock.lock();
        try {
            Implementation implementation = players.get(player);
            if (implementation == null || !implementation.isSupported(Feature.BULK)) {
                return false;
            }

            implementation.sendBulk(network, player, data);
            return true;
        } finally {
            readLock.unlock();
        }
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

        readLock.lock();
        try {
            Implementation implementation = players.get(player);
            if (implementation == null || !implementation.isSupported(Feature.ENTITY_PROPERTIES)) {
                return false;
            }

            implementation.sendEntityProperties(network, player, entity, ticks);
            return true;
        } finally {
            readLock.unlock();
        }
    }

    public void unregister() {
        writeLock.lock();
        try {
            implementations.clear();
            playerListener.unregister();
        } finally {
            writeLock.unlock();
        }
    }
}
