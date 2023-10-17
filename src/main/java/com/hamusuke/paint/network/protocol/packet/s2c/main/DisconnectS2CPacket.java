package com.hamusuke.paint.network.protocol.packet.s2c.main;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class DisconnectS2CPacket implements Packet<ClientCommonPacketListener> {
    private final String msg;

    public DisconnectS2CPacket(String msg) {
        this.msg = msg;
    }

    public DisconnectS2CPacket() {
        this("");
    }

    public DisconnectS2CPacket(IntelligentByteBuf byteBuf) {
        this.msg = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }

    public String getMsg() {
        return this.msg;
    }
}
