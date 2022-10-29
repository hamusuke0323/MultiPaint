package com.hamusuke.paint.network.listener;

import com.hamusuke.paint.network.channel.Connection;

public interface PacketListener {
    void onDisconnected();

    Connection getConnection();

    default void tick() {
    }

    default boolean shouldCrashOnException() {
        return true;
    }
}
