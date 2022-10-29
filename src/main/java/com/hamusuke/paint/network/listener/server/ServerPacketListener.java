package com.hamusuke.paint.network.listener.server;

import com.hamusuke.paint.network.listener.PacketListener;

public interface ServerPacketListener extends PacketListener {
    @Override
    default boolean shouldCrashOnException() {
        return false;
    }
}
