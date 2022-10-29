package com.hamusuke.paint.network.protocol.packet.c2s.main.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class SyncLinesC2SPacket implements Packet<ServerCommonPacketListener> {
    public SyncLinesC2SPacket() {
    }

    public SyncLinesC2SPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleSyncLinesPacket(this);
    }
}
