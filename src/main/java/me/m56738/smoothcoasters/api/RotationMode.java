package me.m56738.smoothcoasters.api;

public enum RotationMode {
    /**
     * Don't rotate the camera.
     */
    NONE,
    /**
     * Rotate the camera (don't change yaw/pitch).
     */
    CAMERA,
    /**
     * Rotate the player (change yaw/pitch).
     * Makes it possible to interact with the world.
     * Might cause problems with anti-cheat plugins.
     */
    PLAYER
}
