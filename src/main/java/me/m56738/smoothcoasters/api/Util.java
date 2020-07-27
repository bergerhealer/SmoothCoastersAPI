package me.m56738.smoothcoasters.api;

import java.nio.ByteBuffer;

class Util {
    public static int readVarInt(ByteBuffer buffer) {
        int length = 0;
        int result = 0;
        byte b;
        do {
            b = buffer.get();
            result |= (b & 0x7F) << (length * 7);
            length++;
            if (length > 5) {
                throw new IllegalArgumentException();
            }
        } while ((b & 0x80) != 0);

        return result;
    }

    public static String readString(ByteBuffer buffer) {
        int length = readVarInt(buffer);
        byte[] data = new byte[length];
        buffer.get(data);
        return new String(data);
    }
}
