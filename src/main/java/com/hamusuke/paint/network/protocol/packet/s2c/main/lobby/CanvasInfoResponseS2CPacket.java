package com.hamusuke.paint.network.protocol.packet.s2c.main.lobby;

import com.google.common.collect.ImmutableList;
import com.hamusuke.paint.canvas.CanvasInfo;
import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.canvas.ServerCanvas;

import java.io.IOException;
import java.util.List;

public class CanvasInfoResponseS2CPacket implements Packet<ClientCommonPacketListener> {
    private final List<CanvasInfo> info;

    public CanvasInfoResponseS2CPacket(List<CanvasInfo> info) {
        this.info = info;
    }

    public CanvasInfoResponseS2CPacket(IntelligentByteBuf byteBuf) {
        this.info = byteBuf.readList(CanvasInfo::new, ImmutableList::copyOf);
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeList(this.info, CanvasInfo::pack);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleCanvasInfoResponse(this);
    }

    public List<CanvasInfo> getInfo() {
        return this.info;
    }
}
