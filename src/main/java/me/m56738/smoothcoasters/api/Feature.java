package me.m56738.smoothcoasters.api;

public enum Feature {
    /**
     * Change the first-person camera rotation of a player
     */
    ROTATION,
    /**
     * Rotate an entity for a player
     */
    @Deprecated
    ENTITY_ROTATION,
    /**
     * Update entity properties
     */
    @Deprecated
    ENTITY_PROPERTIES,
    /**
     * Change the camera rotation limit
     */
    ROTATION_LIMIT
}
