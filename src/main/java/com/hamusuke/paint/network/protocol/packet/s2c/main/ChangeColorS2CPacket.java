package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.network.ServerPainter;

import java.awt.*;
import java.io.IOException;

public class ChangeColorS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;
    private final Color color;

    public ChangeColorS2CPacket(ServerPainter serverPainter) {
        this.id = serverPainter.getId();
        this.color = serverPainter.getColor();
    }

    public ChangeColorS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVariableInt();
        this.color = new Color(byteBuf.readVariableInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeVariableInt(this.id);
        byteBuf.writeVariableInt(this.color.getRGB());
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChangeColorPacket(this);
    }

    public int getId() {
        return this.id;
    }

    public Color getColor() {
        return this.color;
    }
}
