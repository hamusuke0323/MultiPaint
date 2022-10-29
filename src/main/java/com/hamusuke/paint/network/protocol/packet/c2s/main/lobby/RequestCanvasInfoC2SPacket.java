package com.hamusuke.paint.network.protocol.packet.c2s.main.lobby;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class RequestCanvasInfoC2SPacket implements Packet<ServerCommonPacketListener> {
    public RequestCanvasInfoC2SPacket() {
    }

    public RequestCanvasInfoC2SPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleRequestCanvasInfo(this);
    }
}
