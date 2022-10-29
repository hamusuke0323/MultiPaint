package com.hamusuke.paint.network.protocol.packet.c2s.main.canvas;

import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LineC2SPacket implements Packet<ServerCommonPacketListener> {
    private final LineData line;

    public LineC2SPacket(LineData line) {
        this.line = line;
    }

    public LineC2SPacket(IntelligentByteBuf byteBuf) {
        this.line = LineData.unpack(byteBuf);
    }

    public void write(IntelligentByteBuf byteBuf) {
        this.line.pack(byteBuf);
    }

    public LineData getLineData() {
        return this.line;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleLinePacket(this);
    }
}
