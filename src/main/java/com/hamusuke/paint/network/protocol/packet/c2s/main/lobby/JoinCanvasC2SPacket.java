package com.hamusuke.paint.network.protocol.packet.c2s.main.lobby;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class JoinCanvasC2SPacket implements Packet<ServerCommonPacketListener> {
    private final int canvasId;

    public JoinCanvasC2SPacket(int canvasId) {
        this.canvasId = canvasId;
    }

    public JoinCanvasC2SPacket(IntelligentByteBuf byteBuf) {
        this.canvasId = byteBuf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeVariableInt(this.canvasId);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleJoinCanvas(this);
    }

    public int getCanvasId() {
        return this.canvasId;
    }
}
