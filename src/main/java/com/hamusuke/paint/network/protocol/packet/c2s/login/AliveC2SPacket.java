package com.hamusuke.paint.network.protocol.packet.c2s.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class AliveC2SPacket implements Packet<ServerLoginPacketListener> {
    public AliveC2SPacket() {
    }

    public AliveC2SPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.onPing(this);
    }
}
