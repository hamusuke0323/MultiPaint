package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LeavePainterS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int id;

    public LeavePainterS2CPacket(int id) {
        this.id = id;
    }

    public LeavePainterS2CPacket(IntelligentByteBuf byteBuf) {
        this.id = byteBuf.readVarInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeavePacket(this);
    }

    public int getId() {
        return this.id;
    }
}
