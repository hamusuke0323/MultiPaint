package com.hamusuke.paint.network.protocol.packet.s2c.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
    public LoginDisconnectS2CPacket() {
    }

    public LoginDisconnectS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onDisconnect(this);
    }
}
