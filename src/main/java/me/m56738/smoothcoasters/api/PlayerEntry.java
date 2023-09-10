package me.m56738.smoothcoasters.api;

import me.m56738.smoothcoasters.api.implementation.Implementation;

class PlayerEntry {
    private Implementation implementation;
    private String version;

    public Implementation getImplementation() {
        return implementation;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
