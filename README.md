# SmoothCoasters API

This library allows Bukkit plugin developers to support [SmoothCoasters](https://www.curseforge.com/minecraft/mc-mods/smoothcoasters).

## Maven

SmoothCoastersAPI is available in the TrainCarts Maven repository:
```xml
<repository>
    <id>mgdev-repo</id>
    <url>https://ci.mg-dev.eu/plugin/repository/everything/</url>
</repository>
```

```xml
<dependency>
    <groupId>me.m56738</groupId>
    <artifactId>SmoothCoastersAPI</artifactId>
    <version>1.10</version>
</dependency>
```

The API has to be [shaded](https://maven.apache.org/plugins/maven-shade-plugin/) into your plugin and relocated - it's not a plugin on its own. 

## Usage

Create a `new SmoothCoastersAPI(this)` object in `onEnable()` and store it.
Call its `unregister()` method in `onDisable()`.

### Rotation

The camera rotation feature uses quaternions to prevent gimbal lock.

Use `setRotation(null, player, x, y, z, w, ticks)` to set the rotation.

| Parameter  | Type   | Description                            |
| ---------- | ------ | -------------------------------------- |
| player     | Player | Player                                 |
| x, y, z, w | float  | Quaternion fields                      |
| ticks      | byte   | Duration of the interpolation in ticks |

The value 3 is recommended for `ticks`.
Use `resetRotation(null, player)` to reset the rotation (usually when the player leaves their vehicle).

### Network interface

Most methods in the `SmoothCoastersAPI` take a `NetworkInterface` as the first parameter.
Implementing your own `NetworkInterface` allows you to customize how plugin messages are sent to the client.
For example, TrainCarts uses this to send SmoothCoasters packets in the same bundle packet as other packets.

You probably don't need this, so you can just set the first parameter to `null` to use the default implementation which simply calls`Player#sendPluginMessage`.
