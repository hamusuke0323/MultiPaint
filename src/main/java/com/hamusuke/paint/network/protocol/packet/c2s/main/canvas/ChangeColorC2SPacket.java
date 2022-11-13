package com.hamusuke.paint.network.protocol.packet.c2s.main.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.awt.*;
import java.io.IOException;

public class ChangeColorC2SPacket implements Packet<ServerCommonPacketListener> {
    private final Color color;

    public ChangeColorC2SPacket(Color color) {
        this.color = color;
    }

    public ChangeColorC2SPacket(IntelligentByteBuf byteBuf) {
        this.color = byteBuf.readColor();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeColor(this.color);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleChangeColorPacket(this);
    }

    public Color getColor() {
        return this.color;
    }
}
