package com.hamusuke.paint.network.protocol.packet.s2c.main.lobby;

import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.network.ServerPainter;

import java.io.IOException;

public class JoinCanvasS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;
    private final CanvasInfo info;

    public JoinCanvasS2CPacket(ServerPainter serverPainter, CanvasInfo info) {
        this.id = serverPainter.getId();
        this.info = info;
    }

    public JoinCanvasS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVariableInt();
        this.info = new CanvasInfo(byteBuf);
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeVariableInt(this.id);
        this.info.pack(byteBuf);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleJoinCanvas(this);
    }

    public int getId() {
        return this.id;
    }

    public CanvasInfo getInfo() {
        return this.info;
    }
}
