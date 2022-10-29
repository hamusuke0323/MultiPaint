package com.hamusuke.paint.network.protocol.packet.c2s.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class PingC2SPacket implements Packet<ServerCommonPacketListener> {
    private final long clientTime;

    public PingC2SPacket(long clientTime) {
        this.clientTime = clientTime;
    }

    public PingC2SPacket(IntelligentByteBuf byteBuf) {
        this.clientTime = byteBuf.readLong();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handlePingPacket(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}
