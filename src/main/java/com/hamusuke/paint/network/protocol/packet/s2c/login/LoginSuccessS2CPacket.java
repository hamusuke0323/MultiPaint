package com.hamusuke.paint.network.protocol.packet.s2c.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

public class LoginSuccessS2CPacket implements Packet<ClientLoginPacketListener> {
    public LoginSuccessS2CPacket() {
    }

    public LoginSuccessS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.onSuccess(this);
    }
}
