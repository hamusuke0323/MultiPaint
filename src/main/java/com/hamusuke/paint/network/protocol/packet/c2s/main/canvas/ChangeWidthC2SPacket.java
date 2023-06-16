package com.hamusuke.paint.network.protocol.packet.c2s.main.canvas;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class ChangeWidthC2SPacket implements Packet<ServerCommonPacketListener> {
    private final float width;

    public ChangeWidthC2SPacket(float width) {
        this.width = width;
    }

    public ChangeWidthC2SPacket(IntelligentByteBuf byteBuf) {
        this.width = byteBuf.readFloat();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeFloat(this.width);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleChangeWidthPacket(this);
    }

    public float getWidth() {
        return this.width;
    }
}
