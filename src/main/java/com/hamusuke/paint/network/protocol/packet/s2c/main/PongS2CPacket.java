package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class PongS2CPacket implements Packet<ClientCommonPacketListener> {
    private final long clientTime;

    public PongS2CPacket(long clientTime) {
        this.clientTime = clientTime;
    }

    public PongS2CPacket(IntelligentByteBuf byteBuf) {
        this.clientTime = byteBuf.readLong();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePongPacket(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}
