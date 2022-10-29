package com.hamusuke.paint.network.protocol.packet.c2s.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class RTTC2SPacket implements Packet<ServerCommonPacketListener> {
    private final int rtt;

    public RTTC2SPacket(int rtt) {
        this.rtt = rtt;
    }

    public RTTC2SPacket(IntelligentByteBuf buf) {
        this.rtt = buf.readVarInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.rtt);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleRTTPacket(this);
    }

    public int getRtt() {
        return this.rtt;
    }
}
