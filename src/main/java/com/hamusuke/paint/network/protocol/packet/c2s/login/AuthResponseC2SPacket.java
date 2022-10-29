package com.hamusuke.paint.network.protocol.packet.c2s.login;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;
import com.hamusuke.paint.network.listener.server.ServerLoginPacketListener;
import com.hamusuke.paint.network.protocol.packet.Packet;

import java.io.IOException;

public class AuthResponseC2SPacket implements Packet<ServerLoginPacketListener> {
    private final String pwd;

    public AuthResponseC2SPacket(String pwd) {
        this.pwd = pwd;
    }

    public AuthResponseC2SPacket(IntelligentByteBuf byteBuf) {
        this.pwd = byteBuf.readString();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) throws IOException {
        byteBuf.writeString(this.pwd);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.onAuth(this);
    }

    public String getPwd() {
        return this.pwd;
    }
}
