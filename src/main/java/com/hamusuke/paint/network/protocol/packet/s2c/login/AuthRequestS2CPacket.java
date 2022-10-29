package com.hamusuke.paint.network.protocol.packet.s2c.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class AuthRequestS2CPacket implements Packet<ClientLoginPacketListener> {
    public AuthRequestS2CPacket() {
    }

    public AuthRequestS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onAuth(this);
    }
}
