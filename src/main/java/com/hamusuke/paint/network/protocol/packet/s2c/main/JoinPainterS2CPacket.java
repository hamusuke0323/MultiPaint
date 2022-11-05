package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;
import com.hamusuke.paint.server.network.ServerPainter;

import java.util.UUID;

public class JoinPainterS2CPacket implements Packet<ClientCommonPacketListener> {
    private final UUID uuid;
    private final int id;
    private final boolean others;

    public JoinPainterS2CPacket(ServerPainter serverPainter, boolean others) {
        this.uuid = serverPainter.getUuid();
        this.id = serverPainter.getId();
        this.others = others;
    }

    public JoinPainterS2CPacket(IntelligentByteBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.id = byteBuf.readVariableInt();
        this.others = byteBuf.readBoolean();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeUUID(this.uuid);
        byteBuf.writeVariableInt(this.id);
        byteBuf.writeBoolean(this.others);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleJoinPacket(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getId() {
        return this.id;
    }

    public boolean isOthers() {
        return this.others;
    }
}
