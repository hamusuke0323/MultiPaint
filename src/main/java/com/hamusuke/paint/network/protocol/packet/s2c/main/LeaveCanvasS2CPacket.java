package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.network.ServerPainter;

import java.io.IOException;

public class LeaveCanvasS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;

    public LeaveCanvasS2CPacket(ServerPainter serverPainter) {
        this.id = serverPainter.getId();
    }

    public LeaveCanvasS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeVariableInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeaveCanvasPacket(this);
    }

    public int getId() {
        return this.id;
    }
}
