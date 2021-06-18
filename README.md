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
    <version>1.4</version>
</dependency>
```

The API has to be [shaded](https://maven.apache.org/plugins/maven-shade-plugin/) into your plugin - it's not a plugin on its own. 

## Usage

Create a `new SmoothCoastersAPI(this)` object in `onEnable()` and store it.
Call its `unregister()` method in `onDisable()`.

### Rotation

The camera rotation feature uses quaternions to prevent gimbal lock.

Use `setRotation(player, x, y, z, w, ticks)` to set the rotation.

| Parameter  | Type   | Description                            |
| ---------- | ------ | -------------------------------------- |
| player     | Player | Player                                 |
| x, y, z, w | float  | Quaternion fields                      |
| ticks      | byte   | Duration of the interpolation in ticks |

A value of 3 is recommended for `ticks`.
Use `resetRotation(player)` to reset the rotation (usually when the player leaves their vehicle).

### Bulk Packets

Packets are not synchronized to client ticks.
This means that if you send 2 packets, the client could process them in different ticks.
To prevent this, SmoothCoasters offers a way to package multiple packets into one and process all of them in the same tick.
Use `sendBulk(player, data)` to send a bulk packet. The format of the `data` byte array is described below:

#### Packet format

* Number of packets (VarInt)
* Packet data (repeated for every packet):
    * Length of the encoded packet data (VarInt)
    * Packet type ID (VarInt)
    * Encoded packet data
