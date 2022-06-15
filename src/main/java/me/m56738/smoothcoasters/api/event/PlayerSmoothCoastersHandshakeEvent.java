package me.m56738.smoothcoasters.api.event;

import me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The SmoothCoasters handshake was completed successfully and an implementation was chosen.
 */
public class PlayerSmoothCoastersHandshakeEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Implementation implementation;
    private final String version;

    public PlayerSmoothCoastersHandshakeEvent(Player who, Implementation implementation, String version) {
        super(who);
        this.implementation = implementation;
        this.version = version;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the version of the used implementation
     */
    public byte getImplementationVersion() {
        return implementation.getVersion();
    }

    /**
     * @return the SmoothCoasters version of the player - useful for update notifications
     */
    public String getVersion() {
        return version;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
