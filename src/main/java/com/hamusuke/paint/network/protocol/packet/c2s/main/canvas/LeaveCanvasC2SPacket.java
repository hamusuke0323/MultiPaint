package com.hamusuke.paint.network.protocol.packet.c2s.main.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class LeaveCanvasC2SPacket implements Packet<ServerCommonPacketListener> {
    public LeaveCanvasC2SPacket() {
    }

    public LeaveCanvasC2SPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleLeaveCanvasPacket(this);
    }
}
