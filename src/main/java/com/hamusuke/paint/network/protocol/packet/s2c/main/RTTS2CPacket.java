package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class RTTS2CPacket implements Packet<ClientCommonPacketListener> {
    private final int painterId;
    private final int rtt;

    public RTTS2CPacket(int painterId, int rtt) {
        this.painterId = painterId;
        this.rtt = rtt;
    }

    public RTTS2CPacket(IntelligentByteBuf byteBuf) {
        this.painterId = byteBuf.readVarInt();
        this.rtt = byteBuf.readVarInt();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.painterId);
        byteBuf.writeVarInt(this.rtt);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRTTPacket(this);
    }

    public int getPainterId() {
        return this.painterId;
    }

    public int getRtt() {
        return this.rtt;
    }
}
