package com.hamusuke.paint.network.protocol.packet.s2c.main.canvas;

import com.hamusuke.paint.network.LineData;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LineS2CPacket implements Packet<ClientCommonPacketListener> {
    private final LineData line;

    public LineS2CPacket(LineData line) {
        this.line = line;
    }

    public LineS2CPacket(IntelligentByteBuf byteBuf) {
        this.line = LineData.unpack(byteBuf);
    }

    public void write(IntelligentByteBuf byteBuf) {
        this.line.pack(byteBuf);
    }

    public LineData getLine() {
        return this.line;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLinePacket(this);
    }
}
