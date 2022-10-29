package com.hamusuke.paint.network.protocol.packet.s2c.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.client.ClientLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class AliveS2CPacket implements Packet<ClientLoginPacketListener> {
    public AliveS2CPacket() {
    }

    public AliveS2CPacket(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
    }
}
