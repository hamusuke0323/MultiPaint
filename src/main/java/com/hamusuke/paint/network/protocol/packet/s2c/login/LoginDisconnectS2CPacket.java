package com.hamusuke.paint.network.protocol.packet.s2c.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
    private final String msg;

    public LoginDisconnectS2CPacket(String msg) {
        this.msg = msg;
    }

    public LoginDisconnectS2CPacket() {
        this("");
    }

    public LoginDisconnectS2CPacket(IntelligentByteBuf byteBuf) {
        this.msg = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onDisconnect(this);
    }

    public String getMsg() {
        return this.msg;
    }
}
