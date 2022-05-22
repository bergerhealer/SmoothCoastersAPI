package me.m56738.smoothcoasters.api;

public enum Feature {
    /**
     * Change the first-person camera rotation of a player
     */
    ROTATION,
    /**
     * Send multiple packets at once to force processing them in the same tick
     */
    @Deprecated
    BULK,
    /**
     * Rotate an entity for a player
     */
    ENTITY_ROTATION,
    /**
     * Update entity properties
     */
    ENTITY_PROPERTIES,
    /**
     * Change the camera rotation mode
     */
    @Deprecated
    ROTATION_MODE,
    /**
     * Change the camera rotation limit
     */
    ROTATION_LIMIT
}
