package com.hamusuke.paint.network.protocol.packet.s2c.main.lobby;

import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class JoinCanvasS2CPacket implements Packet<ClientCommonPacketListener> {
    private final CanvasInfo info;
    private final int width;
    private final int height;

    public JoinCanvasS2CPacket(CanvasInfo info, int width, int height) {
        this.info = info;
        this.width = width;
        this.height = height;
    }

    public JoinCanvasS2CPacket(IntelligentByteBuf byteBuf) {
        this.info = new CanvasInfo(byteBuf);
        this.width = byteBuf.readVariableInt();
        this.height = byteBuf.readVariableInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        this.info.pack(byteBuf);
        byteBuf.writeVariableInt(this.width);
        byteBuf.writeVariableInt(this.height);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleJoinCanvas(this);
    }

    public CanvasInfo getInfo() {
        return this.info;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
