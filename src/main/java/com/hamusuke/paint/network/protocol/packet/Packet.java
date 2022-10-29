package com.hamusuke.paint.network.protocol.packet;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.PacketListener;

import java.io.IOException;

public interface Packet<T extends PacketListener> {
    void write(IntelligentByteBuf byteBuf) throws IOException;

    void handle(T listener);
}
