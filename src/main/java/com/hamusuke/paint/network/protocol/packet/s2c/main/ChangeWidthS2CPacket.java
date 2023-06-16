package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.network.ServerPainter;

import java.io.IOException;

public class ChangeWidthS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;
    private final float width;

    public ChangeWidthS2CPacket(ServerPainter painter) {
        this.id = painter.getId();
        this.width = painter.getWidth();
    }

    public ChangeWidthS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVariableInt();
        this.width = byteBuf.readFloat();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeVariableInt(this.id);
        byteBuf.writeFloat(this.width);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChangeWidthPacket(this);
    }

    public int getId() {
        return this.id;
    }

    public float getWidth() {
        return this.width;
    }
}
