package com.hamusuke.paint.network.protocol.packet.c2s.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class DisconnectC2SPacket implements Packet<ServerCommonPacketListener> {
    public DisconnectC2SPacket() {
    }

    public DisconnectC2SPacket(IntelligentByteBuf ignored) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleDisconnect(this);
    }
}
